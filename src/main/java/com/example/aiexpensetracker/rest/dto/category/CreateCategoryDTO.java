package com.example.aiexpensetracker.rest.dto.category;

import jakarta.validation.constraints.NotBlank;

public class CreateCategoryDTO {
    @NotBlank(message = "Category name cannot be empty")
    private String name;
    private String description;

    // getters & setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
