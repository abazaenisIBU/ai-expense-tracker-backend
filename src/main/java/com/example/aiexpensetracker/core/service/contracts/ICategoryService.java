package com.example.aiexpensetracker.core.service.contracts;

import com.example.aiexpensetracker.rest.dto.category.CategoryResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CreateCategoryDTO;
import com.example.aiexpensetracker.rest.dto.category.UpdateCategoryDTO;

import java.util.List;

public interface ICategoryService {
    List<CategoryResponseDTO> getAllCategoriesByUser(String userEmail);
    CategoryResponseDTO createCategory(CreateCategoryDTO dto, String userEmail);

    CategoryResponseDTO updateCategory(String email, Long categoryId, UpdateCategoryDTO dto);
    void deleteCategory(String email, Long categoryId);
}

