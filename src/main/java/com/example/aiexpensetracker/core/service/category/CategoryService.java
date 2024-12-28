package com.example.aiexpensetracker.core.service.category;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.manager.IRepositoryManager;
import com.example.aiexpensetracker.exception.category.CategoryNotFoundException;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.category.CategoryResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CreateCategoryDTO;
import com.example.aiexpensetracker.rest.dto.category.UpdateCategoryDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService implements ICategoryService {
    private final IRepositoryManager repositoryManager;

    public CategoryService(IRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public List<CategoryResponseDTO> getAllCategoriesByUser(String userEmail) {
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email " + userEmail + " not found."));

        return repositoryManager.getCategoryRepository()
                .findByUser(user)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCategoryNamesByUser(String userEmail) {
        return getAllCategoriesByUser(userEmail)
                .stream()
                .map(CategoryResponseDTO::getName)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO createCategory(CreateCategoryDTO dto, String userEmail) {
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email " + userEmail + " not found."));

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setUser(user);

        return mapToResponseDTO(repositoryManager.getCategoryRepository().save(category));
    }

    @Override
    public CategoryResponseDTO updateCategory(String email, Long categoryId, UpdateCategoryDTO dto) {
        User user = repositoryManager.getUserRepository()
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));

        Category category = repositoryManager.getCategoryRepository()
                .findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new CategoryNotFoundException("Category does not belong to user with email " + email + ".");
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setUpdatedAt(LocalDateTime.now());

        return mapToResponseDTO(repositoryManager.getCategoryRepository().save(category));
    }

    @Override
    public void deleteCategory(String email, Long categoryId) {
        User user = repositoryManager.getUserRepository()
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));

        Category category = repositoryManager.getCategoryRepository()
                .findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new CategoryNotFoundException("Category does not belong to user with email " + email + ".");
        }

        repositoryManager.getCategoryRepository().delete(category);
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
