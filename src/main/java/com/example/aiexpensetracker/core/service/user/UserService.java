package com.example.aiexpensetracker.core.service.user;

import com.example.aiexpensetracker.rest.dto.user.CreateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateProfilePictureDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UserResponseDTO;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for handling user-related operations in the AI Expense Tracker application.
 * Provides asynchronous methods for user creation, retrieval, update, profile picture update, and deletion.
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     *
     * @param createUserDTO the CreateUserDTO containing the user's email, first name, and last name.
     * @return a CompletableFuture containing a UserResponseDTO representing the created user.
     */
    CompletableFuture<UserResponseDTO> createUser(CreateUserDTO createUserDTO);

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email of the user to be retrieved.
     * @return a CompletableFuture containing an Optional of UserResponseDTO if the user is found, or an empty Optional if not.
     */
    CompletableFuture<Optional<UserResponseDTO>> getUserByEmail(String email);

    /**
     * Updates an existing user's information.
     *
     * @param id           the ID of the user to be updated.
     * @param updateUserDTO the UpdateUserDTO containing the updated user details such as first name, last name, and email.
     * @return a CompletableFuture containing a UserResponseDTO representing the updated user.
     */
    CompletableFuture<UserResponseDTO> updateUser(Long id, UpdateUserDTO updateUserDTO);

    /**
     * Updates a user's profile picture.
     *
     * @param id                       the ID of the user whose profile picture is to be updated.
     * @param updateProfilePictureDTO   the UpdateProfilePictureDTO containing the new profile picture information.
     * @return a CompletableFuture representing the completion of the profile picture update operation.
     */
    CompletableFuture<Void> updateProfilePicture(Long id, UpdateProfilePictureDTO updateProfilePictureDTO);

    /**
     * Deletes a user from the system.
     *
     * @param id the ID of the user to be deleted.
     * @return a CompletableFuture representing the completion of the delete operation.
     */
    CompletableFuture<Void> deleteUser(Long id);
}
