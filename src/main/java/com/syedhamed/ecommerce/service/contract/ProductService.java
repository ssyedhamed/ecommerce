package com.syedhamed.ecommerce.service.contract;


import com.syedhamed.ecommerce.model.Product;
import com.syedhamed.ecommerce.payload.ProductDTO;
import com.syedhamed.ecommerce.payload.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductsByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    void deleteProduct(Long productId);

    void setImage(Long productId, String filePath);

    void deleteImage(Product product, Boolean isDeleted);

    Product getProductById(Long productId);

    List<ProductDTO> getAllProducts();
}
