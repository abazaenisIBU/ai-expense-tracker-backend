package com.example.aiexpensetracker.core.api.aiservice;

import java.util.List;

public interface AIService {

    String suggestCategory(String description, List<String> existingCategories);

//    Map<String, String> parseReceipt(byte[] imageBytes);
}