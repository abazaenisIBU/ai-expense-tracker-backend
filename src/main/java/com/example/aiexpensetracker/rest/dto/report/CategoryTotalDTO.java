package com.example.aiexpensetracker.rest.dto.report;

import java.math.BigDecimal;

public class CategoryTotalDTO {

    private String categoryName;
    private BigDecimal totalAmount;

    // Default constructor
    public CategoryTotalDTO() {
    }

    // Parameterized constructor
    public CategoryTotalDTO(String categoryName, BigDecimal totalAmount) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // toString method (optional)
    @Override
    public String toString() {
        return "CategoryTotalDTO{" +
                "categoryName='" + categoryName + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
