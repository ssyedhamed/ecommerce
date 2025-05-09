package com.syedhamed.ecommerce.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product { //owning side as per Category entity association
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String description;
    private String productImage;
    private Integer quantity;
    private BigDecimal price; //120$
    private BigDecimal discount; //25%
    private BigDecimal specialPrice; //90 [120-(25/100)*120]
    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude

    private Category category;


    @ManyToOne
    @JoinColumn(name = "seller_id")
    @ToString.Exclude

    private User seller;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<CartItem> cartItems= new ArrayList<>();

}
