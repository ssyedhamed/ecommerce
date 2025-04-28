# Cart Service Implementation

The `CartServiceImpl` class is a service implementation for handling shopping cart operations, such as adding and removing products from the cart, calculating the total price, and managing stock limits. It interacts with the `Cart`, `CartItem`, and `Product` entities to perform its operations. It also includes the logic to prevent concurrent modification issues and ensures that cart data remains consistent during transactions.

## Main Features

### `addProductToCart`
Adds a product to the user's cart or increases its quantity if already present. Handles stock limits and ensures that the requested quantity does not exceed available stock.

**Parameters:**
- `productId`: ID of the product to add to the cart.
- `requestedQuantity`: The quantity of the product to add.
- `note`: A note that can be added to the cart item.

**Returns:**  
A `CartItemDTO` object reflecting the final added quantity.

**Throws:**
- `APIException`: If the product is out of stock or the requested quantity exceeds availability.

### `deleteProductFromCart`
Removes a product (or a specified quantity) from the authenticated user's cart. If the quantity to be removed is greater than or equal to the existing quantity, the entire cart item is removed.

**Parameters:**
- `productId`: ID of the product to remove.
- `quantity`: Quantity to remove from the cart.

**Returns:**  
A message indicating the outcome ("CartItem quantity updated" or "CartItem removed from cart").

**Throws:**
- `IllegalArgumentException`: If the quantity is invalid (null or <= 0).
- `ResourceNotFoundException`: If the cart or cart item is not found.

### `getCart`
Fetches the cart for the currently authenticated user, including all cart items.

**Returns:**  
A `CartDTO` object containing all cart items.

### Helper Methods

#### `getProduct`
Fetches a product by its ID. Throws a `ResourceNotFoundException` if the product is not found.

#### `getTotalPriceOfAllCartItemsInTheCart`
Calculates the total price of all cart items using the latest product prices.

#### `getOrCreateCartItem`
Fetches an existing `CartItem` or creates a new one with default values (quantity 0) if not found.

#### `getOrCreateCart`
Fetches an existing cart for the user or creates a new one if it doesn't exist.

#### `getAvailableStock`
Calculates the available stock for a product, excluding the quantity already locked in other users' carts.

## Transaction Management and Concurrent Modification

The service uses `@Transactional` annotations to manage transactions and ensure consistency when multiple operations are executed on the same entities. To avoid `ConcurrentModificationException`, it manually flushes and clears the entity manager before running read-only queries that involve the same collection or entities being modified.

Example solution to avoid `ConcurrentModificationException`:

```java
@PersistenceContext
private EntityManager entityManager;

// After modifying entities (e.g., adding CartItems)
entityManager.flush();   // Flush pending changes to DB
entityManager.clear();   // Clear managed entities to detach them

// Now safe to perform read-only query
Integer availableStock = getAvailableStock(product);
