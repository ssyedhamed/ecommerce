package com.syedhamed.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer quantity; //+ / -

    private Boolean available = true; // true by default; false if product is either deleted, hidden, or permanently unavailable
    //    For tracking, debugging, and behavior analysis
    private LocalDateTime addedAt;
    private LocalDateTime lastUpdatedAt;
    private String note; // "Please gift wrap this", "Use before my birthday", etc.
    private Boolean isWishlistItem = false; //(if cart also handles saved-for-later items)
    private Boolean inventoryLocked = true;//reserved/held during checkout (but not purchased yet)
    private LocalDateTime lockedAt;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;


}
