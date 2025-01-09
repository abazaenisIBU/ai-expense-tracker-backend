package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.user.CreateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateProfilePictureDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for managing users in the AI Expense Tracker application.
 * Provides endpoints for creating, retrieving, updating, and deleting user accounts, as well as updating profile pictures.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ServiceManager serviceManager;

    /**
     * Constructs a new UserController with the provided ServiceManager.
     *
     * @param serviceManager the ServiceManager instance that provides access to various services.
     */
    public UserController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Creates a new user in the system.
     *
     * @param createUserDTO the request body containing the user details.
     * @return a CompletableFuture containing a ResponseEntity with the created UserResponseDTO and HTTP status 201 (Created).
     */
    @PostMapping
    public CompletableFuture<ResponseEntity<UserResponseDTO>> createUser(
            @Valid @RequestBody CreateUserDTO createUserDTO
    ) {
        return serviceManager
                .getUserService()
                .createUser(createUserDTO)
                .thenApply(created -> {
                    URI location = URI.create("/api/users/" + created.getEmail());
                    return ResponseEntity.created(location).body(created);
                });
    }

    /**
     * Updates the profile picture of an existing user.
     *
     * @param id                    the ID of the user whose profile picture is being updated.
     * @param updateProfilePictureDTO the request body containing the new profile picture details.
     * @return a CompletableFuture containing a ResponseEntity with HTTP status 200 (OK).
     */
    @PostMapping("/{id}/profile-picture")
    public CompletableFuture<ResponseEntity<Void>> updateProfilePicture(
            @PathVariable Long id,
            @RequestBody UpdateProfilePictureDTO updateProfilePictureDTO
    ) {
        return serviceManager
                .getUserService()
                .updateProfilePicture(id, updateProfilePictureDTO)
                .thenApply(ignored -> ResponseEntity.ok().build());
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve.
     * @return a CompletableFuture containing a ResponseEntity with the UserResponseDTO if the user is found, or throws a UserNotFoundException if not found.
     */
    @GetMapping("/{email}")
    public CompletableFuture<ResponseEntity<UserResponseDTO>> getUserByEmail(
            @PathVariable String email
    ) {
        return serviceManager
                .getUserService()
                .getUserByEmail(email)
                .thenApply(optionalUser -> {
                    UserResponseDTO user = optionalUser.orElseThrow(() ->
                            new UserNotFoundException("User with email " + email + " not found."));
                    return ResponseEntity.ok(user);
                });
    }

    /**
     * Updates the details of an existing user.
     *
     * @param id           the ID of the user to update.
     * @param updateUserDTO the request body containing the updated user details.
     * @return a CompletableFuture containing a ResponseEntity with the updated UserResponseDTO.
     */
    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO
    ) {
        return serviceManager
                .getUserService()
                .updateUser(id, updateUserDTO)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     * @return a CompletableFuture containing a ResponseEntity with HTTP status 204 (No Content) if the deletion is successful.
     */
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return serviceManager
                .getUserService()
                .deleteUser(id)
                .thenApply(ignored -> ResponseEntity.noContent().build());
    }
}
