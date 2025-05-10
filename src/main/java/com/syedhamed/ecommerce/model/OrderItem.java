package com.syedhamed.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.syedhamed.ecommerce.payload.ProductSnapshot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private ProductSnapshot productSnapshot;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;
}
