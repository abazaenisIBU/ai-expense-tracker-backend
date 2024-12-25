package com.example.aiexpensetracker.rest.dto.statistics;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    // Constructor
    public ExpenseDTO(Long id, BigDecimal amount, LocalDate date, String description) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Default Constructor
    public ExpenseDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
