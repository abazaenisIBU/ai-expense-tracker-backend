package com.example.aiexpensetracker.rest.dto.statistics;

import com.example.aiexpensetracker.core.model.Expense;

import java.math.BigDecimal;
import java.util.List;

public class CategoryStatisticsDTO {
    private String categoryName;
    private BigDecimal totalAmount;
    private List<ExpenseDTO> expenses;

    // Constructor
    public CategoryStatisticsDTO(String categoryName, BigDecimal totalAmount, List<ExpenseDTO> expenses) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
        this.expenses = expenses;
    }

    // Default Constructor
    public CategoryStatisticsDTO() {}

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

    public List<ExpenseDTO> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseDTO> expenses) {
        this.expenses = expenses;
    }
}
