package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.IServiceManager;
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

    private final IServiceManager serviceManager;

    public UserController(IServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<UserResponseDTO>> createUser(
            @Valid @RequestBody CreateUserDTO createUserDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            UserResponseDTO created = serviceManager.getUserService().createUser(createUserDTO);
            URI location = URI.create("/api/users/" + created.getEmail());
            return ResponseEntity.created(location).body(created);
        });
    }

    @PostMapping("/{id}/profile-picture")
    public CompletableFuture<ResponseEntity<Void>> updateProfilePicture(
            @PathVariable Long id,
            @RequestBody UpdateProfilePictureDTO updateProfilePictureDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            serviceManager.getUserService().updateProfilePicture(id, updateProfilePictureDTO);
            return ResponseEntity.ok().build();
        });
    }

    @GetMapping("/{email}")
    public CompletableFuture<ResponseEntity<UserResponseDTO>> getUserByEmail(
            @PathVariable String email
    ) {
        return CompletableFuture.supplyAsync(() -> {
            UserResponseDTO user = serviceManager.getUserService()
                    .getUserByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));
            return ResponseEntity.ok(user);
        });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            UserResponseDTO updated = serviceManager.getUserService().updateUser(id, updateUserDTO);
            return ResponseEntity.ok(updated);
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            serviceManager.getUserService().deleteUser(id);
            return ResponseEntity.noContent().build();
        });
    }
}


