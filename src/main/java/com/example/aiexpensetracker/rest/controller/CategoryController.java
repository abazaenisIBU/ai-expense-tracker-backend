package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.api.aiservice.AIService;
import com.example.aiexpensetracker.core.service.ServiceManager;
import com.example.aiexpensetracker.rest.dto.category.*;
import com.example.aiexpensetracker.rest.dto.shared.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ServiceManager serviceManager;
    private final AIService aiService;

    public CategoryController(ServiceManager serviceManager, AIService aiService) {
        this.serviceManager = serviceManager;
        this.aiService = aiService;
    }

    @GetMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<ApiResponse<List<CategoryResponseDTO>>>> getAllCategoriesByUser(
            @PathVariable String email
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<CategoryResponseDTO> list = serviceManager
                        .getCategoryService()
                        .getAllCategoriesByUser(email);

                ApiResponse<List<CategoryResponseDTO>> body = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Categories fetched successfully",
                        list
                );
                return ResponseEntity.ok(body);

            } catch (RuntimeException e) {
                // e.g. "User not found"
                ApiResponse<List<CategoryResponseDTO>> error = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        });
    }

    @PostMapping("/suggest-category/{email}")
    public CompletableFuture<ResponseEntity<ApiResponse<CategorySuggestionResponseDTO>>> suggestCategory(
            @RequestParam String description,
            @PathVariable String email
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Fetch existing categories for the user
                List<String> existingCategories = serviceManager.getCategoryService()
                        .getAllCategoriesByUser(email)
                        .stream()
                        .map(CategoryResponseDTO::getName)
                        .collect(Collectors.toList());

                if (existingCategories.isEmpty()) {
                    throw new RuntimeException("User not found or no categories available for the user");
                }

                // Get the suggested category
                Map<String, Object> aiResponse = aiService.suggestCategory(description, existingCategories);

                // Extract values from the AI response
                Integer status = (Integer) aiResponse.get("status");
                String categoryName = (String) aiResponse.get("categoryName");
                Boolean isNew = (Boolean) aiResponse.get("isNew");

                // Prepare the main response DTO
                CategorySuggestionResponseDTO responseDTO = new CategorySuggestionResponseDTO(
                        categoryName,
                        isNew
                );

                if (status == null || status == 400) {
                    // Invalid description or unable to process
                    ApiResponse<CategorySuggestionResponseDTO> errorResponse = new ApiResponse<>(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Invalid description or unable to process the category suggestion.",
                            responseDTO
                    );
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }

                // Success
                ApiResponse<CategorySuggestionResponseDTO> successResponse = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Category suggestion successful",
                        responseDTO
                );
                return ResponseEntity.ok(successResponse);

            } catch (RuntimeException e) {
                ApiResponse<CategorySuggestionResponseDTO> error = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        });
    }


    @PostMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<ApiResponse<CategoryResponseDTO>>> createCategory(
            @Valid @RequestBody CreateCategoryDTO createCategoryDTO,
            @PathVariable String email
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CategoryResponseDTO created = serviceManager
                        .getCategoryService()
                        .createCategory(createCategoryDTO, email);

                ApiResponse<CategoryResponseDTO> success = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.CREATED.value(),
                        "Category created successfully",
                        created
                );

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(success);

            } catch (RuntimeException e) {
                ApiResponse<CategoryResponseDTO> notFound = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        });
    }

    @PutMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<CategoryResponseDTO>>> updateCategory(
            @PathVariable String email,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CategoryResponseDTO updated = serviceManager
                        .getCategoryService()
                        .updateCategory(email, id, updateCategoryDTO);

                ApiResponse<CategoryResponseDTO> success = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Category updated successfully",
                        updated
                );
                return ResponseEntity.ok(success);

            } catch (RuntimeException e) {
                // e.g. "User not found" or "Category not found" or "Category not owned by this user"
                ApiResponse<CategoryResponseDTO> notFound = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);

            }
        });
    }

    @DeleteMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> deleteCategory(
            @PathVariable String email,
            @PathVariable Long id
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                serviceManager.getCategoryService().deleteCategory(email, id);

                ApiResponse<Void> body = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NO_CONTENT.value(),
                        "Category deleted successfully",
                        null
                );
                return ResponseEntity.status(HttpStatus.OK).body(body);

            } catch (RuntimeException e) {
                // "Category not found" or "User not found" or "Not the owner"
                ApiResponse<Void> error = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        });
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        StringBuilder sb = new StringBuilder("Validation error(s): ");

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            sb.append("'")
                    .append(error.getField())
                    .append("' ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });

        ApiResponse<Void> response = new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                sb.toString(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }
}
