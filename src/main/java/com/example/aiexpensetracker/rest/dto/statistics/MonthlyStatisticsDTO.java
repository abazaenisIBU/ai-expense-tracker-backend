package com.example.aiexpensetracker.rest.dto.statistics;

import com.example.aiexpensetracker.core.model.Expense;

import java.math.BigDecimal;
import java.util.List;

public class MonthlyStatisticsDTO {
    private String monthYear;
    private BigDecimal totalAmount;
    private List<ExpenseDTO> expenses;

    // Constructor
    public MonthlyStatisticsDTO(String monthYear, BigDecimal totalAmount, List<ExpenseDTO> expenses) {
        this.monthYear = monthYear;
        this.totalAmount = totalAmount;
        this.expenses = expenses;
    }

    // Default Constructor
    public MonthlyStatisticsDTO() {}

    // Getters and Setters
    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
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
