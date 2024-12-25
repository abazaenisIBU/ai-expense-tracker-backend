package com.example.aiexpensetracker.core.api.aiservice;

import java.util.List;
import java.util.Map;

public interface AIService {

    Map<String, Object> suggestCategory(String description, List<String> existingCategories);
}