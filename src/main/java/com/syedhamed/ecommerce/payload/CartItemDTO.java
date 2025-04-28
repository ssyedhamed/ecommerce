package com.syedhamed.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Integer quantity;
    private String productName; //setProductName() <- cartItem.getProduct().getProductName()
    private String productImage; //setImage() <- cartItem.getProduct().getImage();
    private  Double specialPrice; // cartItem.getProduct().getSpecialPrice();
    private Boolean available = true; // true by default; false if product deleted/out of stock
    //    For tracking, debugging, and behavior analysis
    private LocalDateTime addedAt;
    private LocalDateTime lastUpdatedAt;
    private String note; // "Please gift wrap this", "Use before my birthday", etc.
    private Boolean isWishlistItem = false; //(if cart also handles saved-for-later items)
    private Boolean inventoryLocked = true; //For flash sales or high-demand items:

}
