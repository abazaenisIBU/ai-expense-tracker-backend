package com.example.aiexpensetracker.core.service.category;

import com.example.aiexpensetracker.rest.dto.category.CategoryResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CategorySuggestionResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CreateCategoryDTO;
import com.example.aiexpensetracker.rest.dto.category.UpdateCategoryDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for handling operations related to categories in the AI Expense Tracker application.
 * Provides asynchronous methods to perform CRUD operations and category suggestions for users.
 */
public interface CategoryService {

    /**
     * Retrieves a list of all categories associated with a specific user.
     *
     * @param userEmail the email of the user whose categories are to be retrieved.
     * @return a CompletableFuture containing a list of CategoryResponseDTO objects representing the user's categories.
     */
    CompletableFuture<List<CategoryResponseDTO>> getAllCategoriesByUser(String userEmail);

    /**
     * Retrieves a list of all category names associated with a specific user.
     * Useful for displaying category names in dropdowns or for validation purposes.
     *
     * @param userEmail the email of the user whose category names are to be retrieved.
     * @return a CompletableFuture containing a list of category names as strings.
     */
    CompletableFuture<List<String>> getAllCategoryNamesByUser(String userEmail);

    /**
     * Creates a new category for a user.
     *
     * @param dto       the CreateCategoryDTO containing the name and description of the new category.
     * @param userEmail the email of the user for whom the category is being created.
     * @return a CompletableFuture containing a CategoryResponseDTO representing the created category.
     */
    CompletableFuture<CategoryResponseDTO> createCategory(CreateCategoryDTO dto, String userEmail);

    /**
     * Updates an existing category for a user.
     *
     * @param email      the email of the user who owns the category.
     * @param categoryId the ID of the category to be updated.
     * @param dto        the UpdateCategoryDTO containing the updated name and description.
     * @return a CompletableFuture containing a CategoryResponseDTO representing the updated category.
     */
    CompletableFuture<CategoryResponseDTO> updateCategory(String email, Long categoryId, UpdateCategoryDTO dto);

    /**
     * Deletes a category for a user.
     *
     * @param email      the email of the user who owns the category.
     * @param categoryId the ID of the category to be deleted.
     * @return a CompletableFuture representing the completion of the delete operation.
     */
    CompletableFuture<Void> deleteCategory(String email, Long categoryId);

    /**
     * Suggests a category based on a description provided by the user.
     * Uses AI to match the description to existing categories or suggest a new one.
     *
     * @param description the description of the expense or item to categorize.
     * @param email       the email of the user requesting the category suggestion.
     * @return a CompletableFuture containing a CategorySuggestionResponseDTO with the suggested category information.
     */
    CompletableFuture<CategorySuggestionResponseDTO> suggestCategory(String description, String email);
}
