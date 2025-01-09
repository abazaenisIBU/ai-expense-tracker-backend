package com.example.aiexpensetracker.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a category in the AI Expense Tracker application.
 * Maps to the 'categories' table in the database.
 * A category is associated with a user and can have multiple expenses.
 */
@Entity(name = "categories")
public class Category {

    /**
     * Unique identifier for the category.
     * Auto-generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the category.
     * Cannot be null.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Description of the category.
     * Can be null.
     */
    @Column(name = "description")
    private String description;

    /**
     * The user who owns this category.
     * This is a many-to-one relationship, as one user can have multiple categories.
     * The foreign key is stored in the 'user_email' column.
     */
    @ManyToOne
    @JoinColumn(name = "user_email", nullable = false)
    private User user;

    /**
     * List of expenses associated with this category.
     * This is a one-to-many relationship.
     * Expenses are automatically removed if the category is deleted (orphan removal).
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    /**
     * Timestamp indicating when the category was created.
     * This field is set automatically and cannot be updated.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp indicating when the category was last updated.
     * This field is updated automatically.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Default constructor.
     */
    public Category() {}

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
