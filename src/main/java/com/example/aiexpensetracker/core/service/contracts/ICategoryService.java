package com.example.aiexpensetracker.core.service.contracts;

import com.example.aiexpensetracker.core.model.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategoriesByUser(String userEmail);

    Category createCategory(Category category, String userEmail);

    Category updateCategory(Long id, Category categoryDetails);

    void deleteCategory(Long id);
}
