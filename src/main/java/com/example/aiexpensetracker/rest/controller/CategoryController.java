package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.api.aiservice.AIService;
import com.example.aiexpensetracker.core.service.manager.IServiceManager;
import com.example.aiexpensetracker.rest.dto.category.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final IServiceManager serviceManager;
    private final AIService aiService;

    public CategoryController(IServiceManager serviceManager, AIService aiService) {
        this.serviceManager = serviceManager;
        this.aiService = aiService;
    }

    @GetMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<List<CategoryResponseDTO>>> getAllCategoriesByUser(
            @PathVariable String email
    ) {
        return CompletableFuture.supplyAsync(() -> {
            List<CategoryResponseDTO> categories = serviceManager.getCategoryService().getAllCategoriesByUser(email);
            return ResponseEntity.ok(categories);
        });
    }

    @PostMapping("/user/suggest/{email}")
    public CompletableFuture<ResponseEntity<CategorySuggestionResponseDTO>> suggestCategory(
            @RequestParam String description,
            @PathVariable String email
    ) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> existingCategoryNames = serviceManager.getCategoryService().getAllCategoryNamesByUser(email);
            CategorySuggestionResponseDTO suggestion = aiService.suggestCategory(description, existingCategoryNames);
            return ResponseEntity.ok(suggestion);
        });
    }

    @PostMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CreateCategoryDTO createCategoryDTO,
            @PathVariable String email
    ) {
        return CompletableFuture.supplyAsync(() -> {
            CategoryResponseDTO createdCategory = serviceManager.getCategoryService().createCategory(createCategoryDTO, email);
            return ResponseEntity.status(201).body(createdCategory);
        });
    }

    @PutMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<CategoryResponseDTO>> updateCategory(
            @PathVariable String email,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            CategoryResponseDTO updatedCategory = serviceManager.getCategoryService().updateCategory(email, id, updateCategoryDTO);
            return ResponseEntity.ok(updatedCategory);
        });
    }

    @DeleteMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteCategory(
            @PathVariable String email,
            @PathVariable Long id
    ) {
        return CompletableFuture.supplyAsync(() -> {
            serviceManager.getCategoryService().deleteCategory(email, id);
            return ResponseEntity.noContent().build();
        });
    }
}
