package com.example.aiexpensetracker.api.impl.openai;

import com.example.aiexpensetracker.core.api.aiservice.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAIService implements AIService {
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public Map<String, Object> suggestCategory(String description, List<String> existingCategories) {
        // Updated prompt to ensure OpenAI provides all required fields in the response
        String prompt = """
            The user has the following expense categories: %s.
            Based on this list, suggest the most suitable category for the following expense description: "%s".
            Respond in JSON format with the fields:
            - "categoryName" (string): the suggested category name (can be an existing or a new one).
            - "isNew" (boolean): true if the category is new, false if it matches an existing one.
            - "status" (number): 200 if the category suggestion is successful, or 400 if the description is invalid.
            Example response:
            {
              "categoryName": ...,
              "isNew": ...,
              "status": ...,
              "data": {
                "categoryName": "Health & Fitness",
                "isNew": true,
              }
            }
            If the description cannot be processed, respond with:
            {
              "status": ...,
              "categoryName": ...,
              "isNew": ...,
              "data": null
            }.
            """.formatted(String.join(", ", existingCategories), description);

        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(150)
                .build();

        try {
            String response = openAiService.createCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getText()
                    .trim();

            // Parse the response as JSON
            return objectMapper.readValue(response, HashMap.class);
        } catch (Exception e) {
            // If OpenAI response fails to parse, return a default error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("categoryName", null);
            errorResponse.put("isNew", null);
            errorResponse.put("status", 400);
            return errorResponse;
        }
    }
}
