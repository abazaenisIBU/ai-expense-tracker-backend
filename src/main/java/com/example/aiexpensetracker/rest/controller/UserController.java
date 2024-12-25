package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.ServiceManager;
import com.example.aiexpensetracker.rest.dto.shared.ApiResponse;
import com.example.aiexpensetracker.rest.dto.user.CreateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateProfilePictureDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ServiceManager serviceManager;

    public UserController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ApiResponse<UserResponseDTO>>> createUser(
            @Valid @RequestBody CreateUserDTO createUserDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserResponseDTO created = serviceManager.getUserService().createUser(createUserDTO);

                // Build the success response (JSON body)
                ApiResponse<UserResponseDTO> successResponse = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.CREATED.value(),
                        "User created successfully",
                        created
                );

                URI location = URI.create("/api/users/" + created.getEmail());

                return ResponseEntity
                        .created(location)
                        .body(successResponse);

            } catch (DataIntegrityViolationException e) {
                ApiResponse<UserResponseDTO> conflictResponse = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value(),
                        "Email already in use",
                        null
                );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictResponse);
            }
        });
    }

    @PostMapping("/{id}/profile-picture")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> updateProfilePicture(
            @PathVariable Long id,
            @RequestBody UpdateProfilePictureDTO updateProfilePictureDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                serviceManager.getUserService().updateProfilePicture(id, updateProfilePictureDTO);

                ApiResponse<Void> body = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Profile picture updated",
                        null
                );
                return ResponseEntity.status(HttpStatus.OK).body(body);

            } catch (RuntimeException e) {
                ApiResponse<Void> errorResponse = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        });
    }


    @GetMapping("/{email}")
    public CompletableFuture<ResponseEntity<ApiResponse<UserResponseDTO>>> getUserByEmail(
            @PathVariable String email
    ) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<UserResponseDTO> userOpt = serviceManager.getUserService().getUserByEmail(email);

            if (userOpt.isPresent()) {
                ApiResponse<UserResponseDTO> body = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "User found",
                        userOpt.get()
                );
                return ResponseEntity.ok(body);
            } else {
                ApiResponse<UserResponseDTO> body = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        "User not found",
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
        });
    }


    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<UserResponseDTO>>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserResponseDTO updated = serviceManager.getUserService().updateUser(id, updateUserDTO);

                ApiResponse<UserResponseDTO> body = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "User updated successfully",
                        updated
                );
                return ResponseEntity.ok(body);

            } catch (DataIntegrityViolationException e) {
                ApiResponse<UserResponseDTO> conflictResponse = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value(),
                        "Email already in use",
                        null
                );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictResponse);

            } catch (RuntimeException e) {
                ApiResponse<UserResponseDTO> notFoundResponse = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }
        });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> deleteUser(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                serviceManager.getUserService().deleteUser(id);

                ApiResponse<Void> body = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NO_CONTENT.value(),
                        "User deleted successfully",
                        null
                );
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(body);

            } catch (RuntimeException e) {
                ApiResponse<Void> errorResponse = new ApiResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        });
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        StringBuilder sb = new StringBuilder("Validation error(s): ");

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            sb.append("'")
                    .append(error.getField())
                    .append("' ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });

        ApiResponse<Void> response = new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                sb.toString(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }
}


