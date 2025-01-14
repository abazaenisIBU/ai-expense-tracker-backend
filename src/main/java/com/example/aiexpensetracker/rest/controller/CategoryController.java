package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.rest.dto.category.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for managing categories in the AI Expense Tracker application.
 * Provides endpoints for creating, retrieving, updating, and deleting categories, as well as suggesting categories based on descriptions.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ServiceManager serviceManager;

    /**
     * Constructs a new CategoryController with the provided ServiceManager.
     *
     * @param serviceManager the ServiceManager instance that provides access to various services.
     */
    public CategoryController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Retrieves all categories associated with a specific user.
     *
     * @param email the email of the user whose categories are to be retrieved.
     * @return a CompletableFuture containing a ResponseEntity with a list of CategoryResponseDTO objects.
     */
    @GetMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<List<CategoryResponseDTO>>> getAllCategoriesByUser(
            @PathVariable String email
    ) {
        return serviceManager.getCategoryService()
                .getAllCategoriesByUser(email)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Suggests a category based on a given description.
     *
     * @param description the description of the expense or item to categorize.
     * @param email       the email of the user requesting the category suggestion.
     * @return a CompletableFuture containing a ResponseEntity with a CategorySuggestionResponseDTO.
     */
    @PostMapping("/user/suggest/{email}")
    public CompletableFuture<ResponseEntity<CategorySuggestionResponseDTO>> suggestCategory(
            @RequestParam String description,
            @PathVariable String email
    ) {
        return serviceManager.getCategoryService()
                .suggestCategory(description, email)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Creates a new category for a specific user.
     *
     * @param createCategoryDTO the CreateCategoryDTO containing the category details.
     * @param email             the email of the user for whom the category is being created.
     * @return a CompletableFuture containing a ResponseEntity with the created CategoryResponseDTO and HTTP status 201 (Created).
     */
    @PostMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CreateCategoryDTO createCategoryDTO,
            @PathVariable String email
    ) {
        return serviceManager.getCategoryService()
                .createCategory(createCategoryDTO, email)
                .thenApply(createdCategory -> ResponseEntity.status(201).body(createdCategory));
    }

    /**
     * Updates an existing category for a specific user.
     *
     * @param email             the email of the user who owns the category.
     * @param id                the ID of the category to be updated.
     * @param updateCategoryDTO the UpdateCategoryDTO containing the updated category details.
     * @return a CompletableFuture containing a ResponseEntity with the updated CategoryResponseDTO.
     */
    @PutMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<CategoryResponseDTO>> updateCategory(
            @PathVariable String email,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO
    ) {
        return serviceManager.getCategoryService()
                .updateCategory(email, id, updateCategoryDTO)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Deletes a category for a specific user.
     *
     * @param email the email of the user who owns the category.
     * @param id    the ID of the category to be deleted.
     * @return a CompletableFuture containing a ResponseEntity with HTTP status 204 (No Content) if the deletion is successful.
     */
    @DeleteMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteCategory(
            @PathVariable String email,
            @PathVariable Long id
    ) {
        return serviceManager.getCategoryService()
                .deleteCategory(email, id)
                .thenApply(ignored -> ResponseEntity.noContent().build());
    }
}
