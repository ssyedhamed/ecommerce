package com.syedhamed.ecommerce.repository;

import com.syedhamed.ecommerce.model.Cart;
import com.syedhamed.ecommerce.model.CartItem;
import com.syedhamed.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByProduct(Product product);


    @Query("SELECT COUNT(ci) FROM CartItem ci where ci.inventoryLocked = true AND ci.product.id = :productId AND ci.cart.id != :cartId")
    Integer countLockedItemsByProductAndNotCurrentCart(@Param("productId") Long productId, @Param("cartId") Long cartId);

    Optional<CartItem> findByProductAndCartAndInventoryLockedTrue(Product product, Cart cart);

    List<CartItem> findByInventoryLockedTrueAndLockedAtBefore(LocalDateTime cartItemExpiry);

    List<CartItem> findByProductIdAndInventoryLockedTrue(Long productId);

    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.inventoryLocked = true AND ci.product.id = :productId AND ci.cart.id != :cartId")
    Integer getLockedQuantityInOtherCarts(@Param("productId") Long productId, @Param("cartId") Long cartId);

    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.inventoryLocked = true AND ci.product.id = :productId")
    Integer getLockedQuantityInAllCarts(@Param("productId") Long productId);

}
