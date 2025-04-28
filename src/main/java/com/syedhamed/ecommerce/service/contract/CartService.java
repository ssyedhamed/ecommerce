package com.syedhamed.ecommerce.service.contract;

import com.syedhamed.ecommerce.payload.CartDTO;
import com.syedhamed.ecommerce.payload.CartItemDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;


public interface CartService {

     CartItemDTO addProductToCart(Long productId, Integer requestedQuantity, String note);

    CartItemDTO deleteProductFromCart(@NotNull(message = "Product ID is required") Long productId, @Min(value = 1, message = "Quantity must be at least 1") @Positive Integer quantity);

    CartDTO getCurrentUserCart();

    CartDTO getCartByUserId(Long userId);

    List<CartDTO> getAllUserCarts();
}
