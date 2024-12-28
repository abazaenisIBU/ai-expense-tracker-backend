package com.example.aiexpensetracker.rest.dto.category;

public class CategorySuggestionResponseDTO {
    private String categoryName;
    private Boolean isNew;
    private Integer status;

    public CategorySuggestionResponseDTO() {}

    public CategorySuggestionResponseDTO(String categoryName, Boolean isNew, Integer status) {
        this.categoryName = categoryName;
        this.isNew = isNew;
        this.status = status;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
