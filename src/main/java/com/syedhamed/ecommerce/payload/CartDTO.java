package com.syedhamed.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    // total special prices of all products as per quantity in the cart
    private Double totalSpecialPrice;
    private List<CartItemDTO> cartItems = new ArrayList<>();
    // total amount saved ( total price - total actual prices of all products as per quantity in the cart )
    private Double totalSavedPrice;
}
