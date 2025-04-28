package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.exceptions.APIException;
import com.syedhamed.ecommerce.exceptions.ResourceNotFoundException;
import com.syedhamed.ecommerce.model.Category;
import com.syedhamed.ecommerce.payload.CategoryDTO;
import com.syedhamed.ecommerce.payload.CategoryResponse;
import com.syedhamed.ecommerce.repository.CategoryRepository;
import com.syedhamed.ecommerce.service.contract.CategoryService;
import com.syedhamed.ecommerce.service.contract.PaginationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final PaginationService paginationService;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = paginationService.getPageableObject(pageNumber, pageSize, sortBy, sortOrder);
        Page<Category> categoryPageData = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPageData.getContent();
        CategoryResponse response = paginationService.getPaginatedResponse(categoryPageData, CategoryDTO.class, CategoryResponse.class);

        return response;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        //fetching to see if category with same name already exists
        Category categoryInDB= categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryInDB != null){
            throw new APIException("Category already exists");
        }
        categoryRepository.save(category);
        return  modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {

        Category categoryToUpdate = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category", "cid", categoryId));

        categoryToUpdate.setCategoryName(categoryDTO.getCategoryName());
        categoryRepository.save(categoryToUpdate);
        return new ModelMapper().map(categoryToUpdate, CategoryDTO.class);
    }


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category", "cid", categoryId));

            categoryRepository.delete(category);

    }
}
