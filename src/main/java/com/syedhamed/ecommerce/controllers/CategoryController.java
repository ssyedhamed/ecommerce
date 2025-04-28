package com.syedhamed.ecommerce.controllers;

import com.syedhamed.ecommerce.config.AppConstants;
import com.syedhamed.ecommerce.payload.CategoryDTO;
import com.syedhamed.ecommerce.payload.CategoryResponse;
import com.syedhamed.ecommerce.service.contract.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @GetMapping("/categories")
    public ResponseEntity<CategoryResponse> getCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber, //how to keep params names hidden
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_CATEGORY_ID, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ){
        CategoryResponse categories = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }



    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO =  categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO){

            CategoryDTO category = categoryService.updateCategory(id, categoryDTO);
            return new ResponseEntity<>(category, HttpStatus.OK);

    }
    @DeleteMapping("categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){

        categoryService.deleteCategory(id);
//            return new ResponseEntity<>(HttpStatus.OK);
//            return ResponseEntity.ok("Category deleted");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}
