package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.RepositoryManager;
import com.example.aiexpensetracker.core.service.contracts.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {
    private final RepositoryManager repositoryManager;

    public CategoryService(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public List<Category> getAllCategoriesByUser(String userEmail) {
        User user = repositoryManager.getUserRepository().findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return repositoryManager.getCategoryRepository().findByUser(user);
    }

    public Category createCategory(Category category, String userEmail) {
        User user = repositoryManager.getUserRepository().findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        category.setUser(user);
        return repositoryManager.getCategoryRepository().save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = repositoryManager.getCategoryRepository().findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        return repositoryManager.getCategoryRepository().save(category);
    }

    public void deleteCategory(Long id) {
        repositoryManager.getCategoryRepository().deleteById(id);
    }
}

