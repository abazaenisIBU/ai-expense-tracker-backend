package com.example.aiexpensetracker.core.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing an expense in the AI Expense Tracker application.
 * Maps to the 'expenses' table in the database.
 * Each expense is associated with a user and can optionally be categorized.
 */
@Entity(name = "expenses")
public class Expense {

    /**
     * Unique identifier for the expense.
     * Auto-generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who owns this expense.
     * This is a mandatory many-to-one relationship, with the foreign key stored in the 'user_email' column.
     */
    @ManyToOne
    @JoinColumn(name = "user_email", nullable = false)
    private User user;

    /**
     * The category associated with this expense.
     * This is an optional many-to-one relationship, with the foreign key stored in the 'category_id' column.
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * The amount of money spent in this expense.
     * Cannot be null.
     */
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * The date of the expense.
     * Cannot be null.
     */
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * A description or note about the expense.
     * This field is optional.
     */
    @Column(name = "description")
    private String description;

    /**
     * Timestamp indicating when the expense was created.
     * This field is set automatically and cannot be updated.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp indicating when the expense was last updated.
     * This field is updated automatically.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Default constructor.
     */
    public Expense() {}

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
