package com.syedhamed.ecommerce.service.contract;

import com.syedhamed.ecommerce.payload.CategoryDTO;
import com.syedhamed.ecommerce.payload.CategoryResponse;

public interface CategoryService {
    // all scope
     CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
     // admin
     CategoryDTO createCategory(CategoryDTO categoryDTO);
     // seller + admin
     CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
    // seller + admin
     void deleteCategory(Long categoryId);
}
