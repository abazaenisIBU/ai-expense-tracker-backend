package com.example.aiexpensetracker.api.impl.openai;

import com.example.aiexpensetracker.core.api.aiservice.AIService;
import com.example.aiexpensetracker.rest.dto.category.CategorySuggestionResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import java.util.List;
import java.util.Map;


public class OpenAIService implements AIService {
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public CategorySuggestionResponseDTO suggestCategory(String description, List<String> existingCategories) {
        String prompt = generatePrompt(description, existingCategories);
        CompletionRequest completionRequest = buildCompletionRequest(prompt);

        try {
            String response = fetchOpenAIResponse(completionRequest);
            return parseResponse(response);
        } catch (Exception e) {
            return new CategorySuggestionResponseDTO(null, null, 400); // Default error response
        }
    }

    private CompletionRequest buildCompletionRequest(String prompt) {
        return CompletionRequest.builder()
                .prompt(prompt)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(150)
                .build();
    }

    private String fetchOpenAIResponse(CompletionRequest completionRequest) throws Exception {
        return openAiService.createCompletion(completionRequest)
                .getChoices()
                .get(0)
                .getText()
                .trim();
    }

    private CategorySuggestionResponseDTO parseResponse(String response) throws Exception {
        Map<String, Object> parsed = objectMapper.readValue(response, Map.class);
        String categoryName = (String) parsed.get("categoryName");
        Boolean isNew = (Boolean) parsed.get("isNew");
        Integer status = (Integer) parsed.get("status");

        return new CategorySuggestionResponseDTO(categoryName, isNew, status);
    }

    private String generatePrompt(String description, List<String> existingCategories) {
        return """
            The user has the following expense categories: %s.
            Based on this list, suggest the most suitable category for the following expense description: "%s".
            Respond in JSON format with the fields:
            - "categoryName" (string): the suggested category name (can be an existing or a new one).
            - "isNew" (boolean): true if the category is new, false if it matches an existing one.
            - "status" (number): 200 if the category suggestion is successful, or 400 if the description is invalid
            (be a bit strict when trying to validate description).
            """.formatted(String.join(", ", existingCategories), description);
    }
}