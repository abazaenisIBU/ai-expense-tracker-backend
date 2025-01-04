package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.rest.dto.category.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ServiceManager serviceManager;

    public CategoryController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @GetMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<List<CategoryResponseDTO>>> getAllCategoriesByUser(
            @PathVariable String email
    ) {
        return serviceManager.getCategoryService()
                .getAllCategoriesByUser(email)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/user/suggest/{email}")
    public CompletableFuture<ResponseEntity<CategorySuggestionResponseDTO>> suggestCategory(
            @RequestParam String description,
            @PathVariable String email
    ) {
        return serviceManager.getCategoryService()
                .suggestCategory(description, email)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CreateCategoryDTO createCategoryDTO,
            @PathVariable String email
    ) {
        return serviceManager.getCategoryService()
                .createCategory(createCategoryDTO, email)
                .thenApply(createdCategory -> ResponseEntity.status(201).body(createdCategory));
    }

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
