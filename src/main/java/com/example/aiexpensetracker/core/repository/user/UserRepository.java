package com.example.aiexpensetracker.core.repository.user;

import com.example.aiexpensetracker.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on the User entity.
 * Extends JpaRepository to provide basic database interaction methods.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to be retrieved.
     * @return an Optional containing the User entity if found, or an empty Optional if not.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check for existence.
     * @return true if a user exists with the given email, false otherwise.
     */
    boolean existsByEmail(String email);
}
