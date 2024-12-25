package com.example.aiexpensetracker.rest.dto.category;

public class CategorySuggestionResponseDTO {
    private String categoryName;
    private Boolean isNew;

    public CategorySuggestionResponseDTO() {}

    public CategorySuggestionResponseDTO(String categoryName, Boolean isNew) {
        this.categoryName = categoryName;
        this.isNew = isNew;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }
}
