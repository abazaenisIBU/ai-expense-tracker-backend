package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.RepositoryManager;
import com.example.aiexpensetracker.core.service.contracts.ICategoryService;
import com.example.aiexpensetracker.rest.dto.category.CategoryResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CreateCategoryDTO;
import com.example.aiexpensetracker.rest.dto.category.UpdateCategoryDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService implements ICategoryService {
    private final RepositoryManager repositoryManager;

    public CategoryService(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public List<CategoryResponseDTO> getAllCategoriesByUser(String userEmail) {
        // find user
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Category> categories = repositoryManager
                .getCategoryRepository()
                .findByUser(user);

        return categories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO createCategory(CreateCategoryDTO dto, String userEmail) {
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // build category
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setUser(user);

        Category saved = repositoryManager.getCategoryRepository().save(category);
        return mapToResponseDTO(saved);
    }

    @Override
    public CategoryResponseDTO updateCategory(String email, Long categoryId, UpdateCategoryDTO dto) {
        // 1) Find user by email
        User user = repositoryManager.getUserRepository()
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) Find the category by ID
        Category category = repositoryManager.getCategoryRepository()
                .findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 3) Ensure this category is owned by the same user
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category does not belong to this user");
        }

        // 4) Update fields
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setUpdatedAt(LocalDateTime.now());

        Category updated = repositoryManager.getCategoryRepository().save(category);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteCategory(String email, Long categoryId) {
        // 1) Find user
        User user = repositoryManager.getUserRepository()
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) Find category
        Category category = repositoryManager.getCategoryRepository()
                .findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 3) Check ownership
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category does not belong to this user");
        }

        // 4) Delete
        repositoryManager.getCategoryRepository().delete(category);
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setUserId(category.getUser().getId());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}
