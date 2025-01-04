package com.example.aiexpensetracker.core.service.user;

import com.example.aiexpensetracker.rest.dto.user.CreateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateProfilePictureDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UserResponseDTO;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    CompletableFuture<UserResponseDTO> createUser(CreateUserDTO createUserDTO);

    CompletableFuture<Optional<UserResponseDTO>> getUserByEmail(String email);

    CompletableFuture<UserResponseDTO> updateUser(Long id, UpdateUserDTO updateUserDTO);

    CompletableFuture<Void> updateProfilePicture(Long id, UpdateProfilePictureDTO updateProfilePictureDTO);

    CompletableFuture<Void> deleteUser(Long id);
}
