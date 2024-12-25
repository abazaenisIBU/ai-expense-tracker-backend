package com.example.aiexpensetracker.api.impl.openai;

import com.example.aiexpensetracker.core.api.aiservice.AIService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;

public class OpenAIService implements AIService {
    private final OpenAiService openAiService;

    public OpenAIService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public String suggestCategory(String description, List<String> existingCategories) {
        // Update the prompt to include existing categories
        String prompt = "The user has the following expense categories: " + String.join(", ", existingCategories) +
                ". Based on this list, suggest the most suitable category for the following expense description: \"" +
                description + "\". If no category matches, suggest a new category.";

        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(50)
                .build();

        String suggestedCategory = openAiService.createCompletion(completionRequest)
                .getChoices()
                .get(0)
                .getText()
                .trim();

        // Check if the suggested category matches any existing category
        for (String category : existingCategories) {
            if (category.equalsIgnoreCase(suggestedCategory)) {
                return category; // Return existing category if found
            }
        }

        // Return the new category if no match was found
        return suggestedCategory;
    }
}
