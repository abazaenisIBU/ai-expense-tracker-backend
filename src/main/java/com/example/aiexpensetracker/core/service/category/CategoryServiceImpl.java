package com.example.aiexpensetracker.core.service.category;

import com.example.aiexpensetracker.core.api.aiservice.AIService;
import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManager;
import com.example.aiexpensetracker.exception.category.CategoryNotFoundException;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.category.CategoryResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CategorySuggestionResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CreateCategoryDTO;
import com.example.aiexpensetracker.rest.dto.category.UpdateCategoryDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final RepositoryManager repositoryManager;
    private final AIService aiService;

    public CategoryServiceImpl(RepositoryManager repositoryManager, AIService aiService) {
        this.repositoryManager = repositoryManager;
        this.aiService = aiService;
    }

    @Async
    @Override
    public CompletableFuture<List<CategoryResponseDTO>> getAllCategoriesByUser(String userEmail) {
        return CompletableFuture.supplyAsync(() -> {
            User user = repositoryManager.getUserRepository()
                    .findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("User with email " + userEmail + " not found."));

            return repositoryManager.getCategoryRepository()
                    .findByUser(user)
                    .stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        });
    }

    @Async
    @Override
    public CompletableFuture<List<String>> getAllCategoryNamesByUser(String userEmail) {
        // We can leverage the above method
        return getAllCategoriesByUser(userEmail)
                .thenApply(categoryList ->
                        categoryList.stream()
                                .map(CategoryResponseDTO::getName)
                                .collect(Collectors.toList())
                );
    }

    @Async
    public CompletableFuture<CategorySuggestionResponseDTO> suggestCategory(String description, String email) {
        return getAllCategoryNamesByUser(email)
                .thenApply(existingNames -> {
                    return aiService.suggestCategory(description, existingNames);
                });
    }

    @Async
    @Override
    public CompletableFuture<CategoryResponseDTO> createCategory(CreateCategoryDTO dto, String userEmail) {
        return CompletableFuture.supplyAsync(() -> {
            User user = repositoryManager.getUserRepository()
                    .findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("User with email " + userEmail + " not found."));

            Category category = new Category();
            category.setName(dto.getName());
            category.setDescription(dto.getDescription());
            category.setUser(user);

            Category saved = repositoryManager.getCategoryRepository().save(category);
            return mapToResponseDTO(saved);
        });
    }

    @Async
    @Override
    public CompletableFuture<CategoryResponseDTO> updateCategory(
            String email,
            Long categoryId,
            UpdateCategoryDTO dto
    ) {
        return CompletableFuture.supplyAsync(() -> {
            User user = repositoryManager.getUserRepository()
                    .findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));

            Category category = repositoryManager.getCategoryRepository()
                    .findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));

            if (!category.getUser().getId().equals(user.getId())) {
                throw new CategoryNotFoundException(
                        "Category does not belong to user with email " + email + "."
                );
            }

            category.setName(dto.getName());
            category.setDescription(dto.getDescription());
            category.setUpdatedAt(LocalDateTime.now());

            Category updated = repositoryManager.getCategoryRepository().save(category);
            return mapToResponseDTO(updated);
        });
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteCategory(String email, Long categoryId) {
        return CompletableFuture.runAsync(() -> {
            User user = repositoryManager.getUserRepository()
                    .findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));

            Category category = repositoryManager.getCategoryRepository()
                    .findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));

            if (!category.getUser().getId().equals(user.getId())) {
                throw new CategoryNotFoundException(
                        "Category does not belong to user with email " + email + "."
                );
            }

            repositoryManager.getCategoryRepository().delete(category);
        });
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setUserId(category.getUser().getId());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}
