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

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ServiceManager serviceManager;

    public UserController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

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

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO
    ) {
        return serviceManager
                .getUserService()
                .updateUser(id, updateUserDTO)
                .thenApply(updated -> ResponseEntity.ok(updated));
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return serviceManager
                .getUserService()
                .deleteUser(id)
                .thenApply(ignored -> ResponseEntity.noContent().build());
    }
}
