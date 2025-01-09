package com.example.aiexpensetracker.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a user in the AI Expense Tracker application.
 * Maps to the 'users' table in the database.
 * Each user can have multiple categories associated with them.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User {

    /**
     * Unique identifier for the user.
     * Auto-generated using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's email address.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The user's first name.
     * Cannot be null.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * The user's last name.
     * Cannot be null.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * The user's profile picture.
     * Can store the URL or file name of the picture.
     * This field is optional.
     */
    @Column(name = "profile_picture")
    private String profilePicture;

    /**
     * Timestamp indicating when the user account was created.
     * This field is set automatically and cannot be updated.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp indicating when the user account was last updated.
     * This field is updated automatically.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * List of categories associated with the user.
     * This is a one-to-many relationship, meaning one user can have multiple categories.
     * Categories are automatically removed if the user is deleted (orphan removal).
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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
