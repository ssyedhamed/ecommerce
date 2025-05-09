package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.exceptions.APIException;
import com.syedhamed.ecommerce.exceptions.ResourceNotFoundException;
import com.syedhamed.ecommerce.model.Cart;
import com.syedhamed.ecommerce.model.CartItem;
import com.syedhamed.ecommerce.model.Product;
import com.syedhamed.ecommerce.model.User;
import com.syedhamed.ecommerce.payload.CartDTO;
import com.syedhamed.ecommerce.payload.CartItemDTO;
import com.syedhamed.ecommerce.repository.CartItemRepository;
import com.syedhamed.ecommerce.repository.CartRepository;
import com.syedhamed.ecommerce.repository.ProductRepository;
import com.syedhamed.ecommerce.service.contract.CartService;
import com.syedhamed.ecommerce.util.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
/*
  Service implementation for managing shopping cart operations like
  adding products, calculating total price, handling stock limits, etc.

  For detailed documentation of this service, see:
  <a href="file:docs/cart_service.md">CartServiceImpl Documentation</a>

  This class handles the logic for managing the user's cart, including adding, removing,
  and updating cart items. It also ensures proper inventory checks and updates.
 */
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;

    /**
     * ⚠️ Handles potential ConcurrentModificationException when performing multiple operations
     * (writes followed by reads) on the same entities within a single @Transactional method.
     *
     * <p>❗ Problem:
     * When you modify entities (e.g., add/update CartItems) and later perform a query (e.g., checking stock)
     * within the same transaction, Hibernate attempts to auto-flush pending changes before executing the query.
     * If the changes involve looping or modifying the same collection (like cart items), it may result in
     * a ConcurrentModificationException.
     *
     * <p>✅ Solution:
     * Use the {@link jakarta.persistence.EntityManager} to flush and clear the persistence context manually
     * before running such queries. This ensures Hibernate completes the current persistence state and
     * avoids concurrent modification during flush.
     *
     * <pre>{@code
     *     @PersistenceContext
     *     private EntityManager entityManager;
     *
     *     // After modifying entities (e.g., adding CartItems)
     *     entityManager.flush();   // Flush pending changes to DB
     *     entityManager.clear();   // Clear managed entities to detach them
     *
     *     // Now safe to perform read-only query
     *     Integer availableStock = getAvailableStock(product);
     * }</pre>
     *
     * <p>💡 Note:
     * This pattern is helpful in complex service methods where reads and writes are interleaved,
     * especially when dealing with lazy-loaded associations or bidirectional mappings.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Adds a product to the user's cart or increases its quantity if already present.
     * - If the product is out of stock or requested quantity exceeds availability, throws an exception.
     * - Automatically creates a new cart or cart item if not already present.
     * - Dynamically calculates total cart price using the product's latest price.
     *
     * @param productId         ID of the product to add
     * @param requestedQuantity Quantity the user wants to add
     * @return Updated {@link CartItemDTO} reflecting the final added quantity
     */
    @Override
    @Transactional
    public CartItemDTO addProductToCart(Long productId, Integer requestedQuantity, String note) {


        log.info("▶ Attempting to add product [{}] with quantity [{}] to cart", productId, requestedQuantity);

        Product product = getProduct(productId);
        if (product.getQuantity() == 0) {
            log.warn("⚠ Product [{}] is out of stock", product.getProductName());
            throw new APIException(product.getProductName() + " is not available");
        }
        if (requestedQuantity > product.getQuantity()) {
            log.debug("🔍 Requested [{}] > Total Stock [{}]", requestedQuantity, product.getQuantity());
        }

        User user = authUtil.getAuthenticatedUserFromCurrentContext();
        log.info("✅ Authenticated user [{}] retrieved from security context", user.getEmail());

        Cart cart = getOrCreateCart(user);
        log.info("🛒 Using cart with ID [{}] for user [{}]", cart.getId(), user.getEmail());

        CartItem cartItem = getOrCreateCartItem(product, cart);
        log.info("📌 CartItem fetched or created — ID [{}], quantity [{}]", cartItem.getId(), cartItem.getQuantity());

        cartItem.setLastUpdatedAt(LocalDateTime.now());
        cartItem.setNote(note);
        int existingQty = cartItem.getQuantity();
        int totalRequested = existingQty + requestedQuantity;
        log.info("🔄 Existing quantity: [{}], Total after request: [{}]", existingQty, totalRequested);
        Integer availableStock = getAvailableStock(product);
        log.info("📦 Available stock for product [{}]: [{}]", product.getId(), availableStock);
        if (totalRequested > availableStock) {
            int canBeAdded = availableStock - existingQty;
            if (canBeAdded <= 0) {
                log.warn("⚠ Not enough stock to add more units of [{}]", product.getProductName());
                throw new APIException("Only " + canBeAdded + " unit(s) are available right now.");
            }

            cartItem.setQuantity(existingQty + canBeAdded);
            log.info("✅ Partial add: added [{}] units out of [{}]", canBeAdded, requestedQuantity);
        } else {
            cartItem.setQuantity(totalRequested);
            log.info("✅ Full quantity added to cart: [{}]", totalRequested);
        }
        cartItem.setAvailable(true);
// Add the current CartItem to the in-memory Cart before calculating total price.
// Even though the item is saved to the DB, cart.getCartItems() may not reflect it yet,
// because the Cart entity in memory doesn't auto-refresh its list of items.
// This ensures the new CartItem is included in the total price calculation.
        Optional<CartItem> existingItem = cart.getCartItems()
                .stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(totalRequested); // just update
        } else {
            cart.getCartItems().add(cartItem); // only add if it didn't exist
        }

        log.info("cart size before total price calculation: [{}]", cart.getCartItems().size());
        BigDecimal totalCartPrice = getTotalPriceOfAllCartItemsInTheCart(cart);
        log.info("💰 Updated cart total price: [{}]", totalCartPrice);
        cart.setTotalSpecialPrice(totalCartPrice);
        BigDecimal totalDiscountedPrice = getTotalDiscountedPrice(cart, totalCartPrice);
        cart.setTotalSavedPrice(totalDiscountedPrice);
        cartRepository.save(cart);
        Cart savedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new IllegalStateException("Cart not found after saving"));

        log.info("📦 Cart saved with ID [{}]", savedCart.getId());

        CartItem savedCartItem = savedCart.getCartItems()
                .stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("❌ CartItem for product [{}] not found after saving cart", productId);
                    return new IllegalStateException("CartItem not found in saved cart");
                });

        // Explicit flush to commit changes BEFORE calling again
        entityManager.flush(); // ✅ Forces Hibernate to flush and clear dirty checking
        entityManager.clear(); // ✅ Detach all managed entities

        // Now the second call won’t cause any concurrent modification error
        Integer availableAfter = getAvailableStock(product);
        log.info("✅ Available stock after: {}", availableAfter);
        log.info("✅ CartItem successfully added/updated in cart — ID [{}], quantity [{}]",
                savedCartItem.getId(), savedCartItem.getQuantity());
        // This sets up the mapping ONCE (usually in config or @Bean setup)
        modelMapper.typeMap(CartItem.class, CartItemDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getProduct().getProductName(), CartItemDTO::setProductName);
            mapper.map(src -> src.getProduct().getProductImage(), CartItemDTO::setProductImage);
            mapper.map(src -> src.getProduct().getSpecialPrice(), CartItemDTO::setSpecialPrice);
        });

        // Then when you actually want to convert:


        return modelMapper.map(cartItem, CartItemDTO.class);
    }

    private BigDecimal getTotalDiscountedPrice(Cart cart, BigDecimal totalCartPrice) {
        BigDecimal totalActualPrice = BigDecimal.valueOf(0);
        for (CartItem item : cart.getCartItems()) {
            log.info("Total Actual Price in loop: [{}]", totalActualPrice); //0,200
            BigDecimal totalActualPriceAsPerQuantityOfCurrentItem =  item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            log.info("totalActualPriceAsPerQuantityOfCurrentItem in loop: [{}]", totalActualPriceAsPerQuantityOfCurrentItem);
            //10*20 = 200
            // 2 * 600 = 1200
            totalActualPrice = totalActualPrice.add(totalActualPriceAsPerQuantityOfCurrentItem);
            log.info("totalActualPrice after calculation in current iteration in loop: [{}]", totalActualPrice);
            //0 + 200 = 200
            // 200 + 1200 = 1400
        }
        log.info("Total Actual price as per quantity of all products is [{}]", totalActualPrice);
        return totalActualPrice.subtract(totalCartPrice);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("❌ Product not found with ID [{}]", productId);
                    return new ResourceNotFoundException("Product", "Id", productId);
                });
    }

    @Override
    @Transactional
    /*
      Removes a product (or quantity of a product) from the authenticated user's cart.
      <p>
      If the quantity to be removed is less than the existing quantity, it updates the cart item.
      If the quantity to be removed is equal to or more than the existing quantity, it removes the entire cart item.

      @param productId the ID of the product to be removed
     * @param quantity  the quantity to remove from the cart (must be greater than 0)
     * @return a message indicating the outcome (e.g., "CartItem quantity updated" or "CartItem removed from cart")
     * @throws IllegalArgumentException       if quantity is null or less than or equal to 0
     * @throws ResourceNotFoundException      if the cart or cart item is not found for the current user
     */
    public CartItemDTO deleteProductFromCart(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity to remove must be greater than 0");
        }

        log.info("🧺 Request to remove [{}] units of product ID [{}] from the cart", quantity, productId);

        Product product = getProduct(productId);
        User user = authUtil.getAuthenticatedUserFromCurrentContext();
        log.info("Fetching cart by user...");

        // ⚠️ Be cautious here: printing cartItems may internally call cart.toString(),
        // which references cart.getUser(), which references user.getAddresses() (lazy-loaded).
        // If this happens outside an open Hibernate session, it will throw LazyInitializationException!
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user: ", user.getEmail()));
        log.info("printing cart.getCartItems()");
        log.debug("🧾 Cart items BEFORE update: {}", cart.getCartItems());
        log.debug("💵 Cart total BEFORE update: {}", cart.getTotalSpecialPrice());

        Optional<CartItem> optionalCartItem = cartItemRepository.findByProductAndCartAndInventoryLockedTrue(product, cart);

        if (optionalCartItem.isEmpty()) {
            log.warn("❌ No CartItem found for product [{}] in cart of user [{}] with inventoryLocked = true",
                    product.getId(), user.getEmail());
            throw new ResourceNotFoundException("CartItem", "User Email: ", user.getEmail());
        }

        CartItem cartItem = optionalCartItem.get();
        Integer existingQty = cartItem.getQuantity();

        if (existingQty > quantity) {
            // Partial removal - update quantity
            cartItem.setQuantity(existingQty - quantity);
            cartItem.setLastUpdatedAt(LocalDateTime.now());

            log.debug("✅ Updated quantity for product [{}] from [{}] to [{}]",
                    product.getId(), existingQty, cartItem.getQuantity());

            log.debug("🧾 Cart items AFTER update (pre-save): {}", cart.getCartItems());

            cart.setTotalSpecialPrice(getTotalPriceOfAllCartItemsInTheCart(cart));
            log.debug("💵 Cart total AFTER update: {}", cart.getTotalSpecialPrice());

            CartItem savedCartItem = cartItemRepository.save(cartItem);
            return modelMapper.map(savedCartItem, CartItemDTO.class);
        } else {
            // Full removal
            cart.getCartItems().remove(cartItem);
            cart.setTotalSpecialPrice(getTotalPriceOfAllCartItemsInTheCart(cart));
            cart.setTotalSavedPrice(getTotalDiscountedPrice(cart, cart.getTotalSpecialPrice()));
            Cart updatedCart = cartRepository.save(cart);
            log.info("🗑️ Removed entire CartItem for product [{}] from user [{}]'s cart",
                    product.getId(), user.getEmail());
            log.info("CartItem in cart [{}]", updatedCart.getCartItems().contains(cartItem));
            return null;
        }
    }

    @Override
    public CartDTO getCurrentUserCart() {
        User user = authUtil.getAuthenticatedUserFromCurrentContext();
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "email", user.getEmail()));
        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId: ", userId));

        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    public List<CartDTO> getAllUserCarts() {
        return cartRepository.findAll()
                .stream()
                .map(cart -> modelMapper.map(cart, CartDTO.class))
                .toList();
    }

    /**
     * Calculates the total price of all cart items in the given cart using the latest product prices.
     * (e.g., Amazon-style dynamic pricing)
     *
     * @param cart the user's cart
     * @return total price of all items
     */
    private static BigDecimal getTotalPriceOfAllCartItemsInTheCart(Cart cart) {
        if (cart.getCartItems() == null) {
            return BigDecimal.valueOf(0);
        }
        log.info("cart size : [{}]", cart.getCartItems().size());
        BigDecimal total0 = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            BigDecimal price = cartItem.getProduct().getSpecialPrice();
            BigDecimal qty = BigDecimal.valueOf(cartItem.getQuantity());
            BigDecimal itemTotal = price.multiply(qty);
            log.info("Calculating: {} × {} = {}", price, qty, itemTotal);
            total0 = total0.add(itemTotal);
        }
        log.info("💵 Total using loop: {}", total0);

        BigDecimal total = cart.getCartItems().stream()
                .peek(cartItem -> {
                    BigDecimal price = cartItem.getProduct().getSpecialPrice();
                    BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());
                    log.info("🧾 Product ID: {}, Qty: {}, Price: {}, Total: {}",
                            cartItem.getProduct().getId(),
                            quantity,
                            price,
                            price.multiply(quantity));  // log the price * qty calculation
                })
                .map(cartItem -> cartItem.getProduct().getSpecialPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
//                .mapToDouble(cartItem ->
//                        cartItem.getQuantity() * cartItem.getProduct().getSpecialPrice())
//                .sum();

        log.info("💵 Calculated total cart price: [{}]", total);
        return total;
    }

    /**
     * Returns an existing CartItem for the given product and cart if present and locked;
     * otherwise, creates a new CartItem with default values (quantity 0).
     *
     * @param product the product being added
     * @param cart    the user's cart
     * @return existing or newly initialized CartItem
     */
    private CartItem getOrCreateCartItem(Product product, Cart cart) {
        return cartItemRepository.findByProductAndCartAndInventoryLockedTrue(product, cart)
                .orElseGet(() -> {
                    log.info("🆕 Creating new CartItem for product [{}] in cart [{}]", product.getId(), cart.getId());
                    CartItem newCartItem = new CartItem();
                    newCartItem.setProduct(product);
                    newCartItem.setCart(cart);
                    newCartItem.setQuantity(0);
                    newCartItem.setAddedAt(LocalDateTime.now());
                    newCartItem.setInventoryLocked(true);
                    newCartItem.setLockedAt(LocalDateTime.now());
                    return cartItemRepository.save(newCartItem);
                });
    }

    /**
     * Finds the existing cart for a user or creates and saves a new one.
     *
     * @param user the currently authenticated user
     * @return the user's existing or newly created cart
     */
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    log.info("🆕 No existing cart for user [{}], creating new cart", user.getEmail());
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    Cart saved = cartRepository.save(newCart);
                    /*Even if you initialized CartItems in the Cart entity (= new ArrayList<>()),
                    once Hibernate gets involved, it may replace that field internally
                    with a proxy or just leave it null depending on fetch context
                    and transaction boundaries.
                    saved.getCartItems() may still be null.*/
                    log.info("🛒 New cart saved with ID [{}]", saved.getId());
                    return saved;
                });
    }

    /**
     * Calculates the available stock of a product excluding the quantity already
     * locked by CartItems in other users' carts.
     *
     * @param product the product being checked
     * @return available stock count for this user
     */
    public Integer getAvailableStock(Product product) {
        Integer lockedQty = cartItemRepository.getLockedQuantityInAllCarts(product.getId());
        log.info("🔐 Locked quantity of product [{}] in other carts: [{}]", product.getId(), lockedQty);
        return Math.max(product.getQuantity() - lockedQty, 0);

    }
}

