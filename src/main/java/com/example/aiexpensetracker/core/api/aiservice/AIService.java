package com.example.aiexpensetracker.core.api.aiservice;

import com.example.aiexpensetracker.rest.dto.category.CategorySuggestionResponseDTO;

import java.util.List;

public interface AIService {
    CategorySuggestionResponseDTO suggestCategory(String description, List<String> existingCategories);
}