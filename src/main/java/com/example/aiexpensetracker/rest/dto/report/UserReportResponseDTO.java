package com.example.aiexpensetracker.rest.dto.report;

import com.example.aiexpensetracker.rest.dto.statistics.ExpenseDTO;

import java.math.BigDecimal;
import java.util.List;

public class UserReportResponseDTO {
    private String userEmail;
    private List<ExpenseDTO> expenses;
    private BigDecimal totalAmount;
    private List<CategoryTotalDTO> categoryTotals;

    public UserReportResponseDTO(String userEmail, List<ExpenseDTO> expenses, BigDecimal totalAmount, List<CategoryTotalDTO> categoryTotals) {
        this.userEmail = userEmail;
        this.expenses = expenses;
        this.totalAmount = totalAmount;
        this.categoryTotals = categoryTotals;
    }

    public UserReportResponseDTO() {

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<ExpenseDTO> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseDTO> expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<CategoryTotalDTO> getCategoryTotals() {
        return categoryTotals;
    }

    public void setCategoryTotals(List<CategoryTotalDTO> categoryTotals) {
        this.categoryTotals = categoryTotals;
    }
}
