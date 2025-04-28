package com.syedhamed.ecommerce.repository;

import com.syedhamed.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory_categoryId(Long categoryId);

    List<Product> findByProductNameContainingIgnoreCase(String keyword);
    //returns given product name
    Product findByProductNameIgnoreCase(String productName);
    // checks if product with given product name and category exists or not
    Boolean existsByProductNameIgnoreCaseAndCategory_CategoryId(String productName, Long categoryId);

    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageDetails);

    Page<Product> findByCategory_categoryId(Long categoryId, Pageable pageDetails);
}
