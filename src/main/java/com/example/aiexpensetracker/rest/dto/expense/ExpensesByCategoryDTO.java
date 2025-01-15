package com.example.aiexpensetracker.rest.dto.expense;

import java.util.List;

public class ExpensesByCategoryDTO {
    private String category;
    private List<ExpenseResponseDTO> expenses;

    public ExpensesByCategoryDTO() {}

    public ExpensesByCategoryDTO(String category, List<ExpenseResponseDTO> expenses) {
        this.category = category;
        this.expenses = expenses;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ExpenseResponseDTO> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseResponseDTO> expenses) {
        this.expenses = expenses;
    }
}
