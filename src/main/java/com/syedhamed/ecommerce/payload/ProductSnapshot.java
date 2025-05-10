package com.syedhamed.ecommerce.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.syedhamed.ecommerce.model.OrderItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String productImage;
    private BigDecimal price;
    @OneToOne(mappedBy = "productSnapshot")
    @JsonIgnore
    private OrderItem orderItem;
}
