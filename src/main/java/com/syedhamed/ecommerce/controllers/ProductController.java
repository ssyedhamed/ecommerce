package com.syedhamed.ecommerce.controllers;

import com.syedhamed.ecommerce.config.AppConstants;
import com.syedhamed.ecommerce.model.Product;
import com.syedhamed.ecommerce.payload.ProductDTO;
import com.syedhamed.ecommerce.payload.ProductResponse;
import com.syedhamed.ecommerce.service.contract.FileService;
import com.syedhamed.ecommerce.service.contract.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    FileService fileService;

    //    This should only have an admin scope
    @PostMapping("/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@PathVariable Long categoryId,
                                                 @RequestBody @Valid  ProductDTO productDTO) {
        ProductDTO response = null;
        try {
            response = productService.addProduct(categoryId, productDTO);
        } catch (AccessDeniedException e) {
           return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //admin scope
    @PatchMapping("/products/{productId}/image")
    public ResponseEntity<Map<String, String>> uploadImage(@PathVariable Long productId,
                                                           @RequestParam(value = "image") MultipartFile image) {
        Map<String, String> response = new HashMap<>();
        String imageName = fileService.saveFile(image);
        if(imageName == null){
            return new ResponseEntity<>(Map.of("message", "Image not updated"), HttpStatus.CREATED);
        }
        productService.setImage(productId, imageName);
        response.put("image", imageName);
        response.put("message", "Uploaded Successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //admin scope
    @DeleteMapping("/products/{productId}/image")
    public ResponseEntity<Void> deleteImage(@PathVariable Long productId){
        Product product =  productService.getProductById(productId);
        Boolean isDeleted = fileService.deleteFile(product.getProductImage());
        productService.deleteImage(product, isDeleted); //isDeleted is passed to control not deleted error
        return ResponseEntity.ok().build();
    }

    //admin scope
    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductDTO productDTO) {
        ProductDTO response = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




    //public scope
    @GetMapping("/products/search")
    public ResponseEntity<ProductResponse> getAllProductsByKeyword(
            @RequestParam(required = false) String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber, //how to keep params names hidden
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_CATEGORY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        ProductResponse response = productService
                .getAllProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    //public scope
    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber, //how to keep params names hidden
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_CATEGORY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        ProductResponse response = productService.getProductsByCategoryId(categoryId,pageNumber, pageSize, sortBy, sortOrder );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //admin scope
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        // first delete the file from the system/cloud
        Product product =  productService.getProductById(productId);
         fileService.deleteFile(product.getProductImage());
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
