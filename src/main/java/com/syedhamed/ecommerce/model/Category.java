package com.syedhamed.ecommerce.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
//import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "categories")
public class Category { //inverse side as per Product entity association
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
//    Validation is moved to DTO
//    @NotBlank
//    @Size(min = 5, message = "Name must be more than 5 characters")
    private String categoryName;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> productSet = new HashSet<>();

}
