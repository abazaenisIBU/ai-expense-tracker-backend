package com.example.aiexpensetracker.core.service.category;

import com.example.aiexpensetracker.rest.dto.category.CategoryResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CategorySuggestionResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CreateCategoryDTO;
import com.example.aiexpensetracker.rest.dto.category.UpdateCategoryDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CategoryService {
    CompletableFuture<List<CategoryResponseDTO>> getAllCategoriesByUser(String userEmail);

    CompletableFuture<List<String>> getAllCategoryNamesByUser(String userEmail);

    CompletableFuture<CategoryResponseDTO> createCategory(CreateCategoryDTO dto, String userEmail);

    CompletableFuture<CategoryResponseDTO> updateCategory(String email, Long categoryId, UpdateCategoryDTO dto);

    CompletableFuture<Void> deleteCategory(String email, Long categoryId);

    CompletableFuture<CategorySuggestionResponseDTO> suggestCategory(String description, String email);
}

