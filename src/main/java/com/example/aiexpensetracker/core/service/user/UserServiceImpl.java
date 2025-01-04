package com.example.aiexpensetracker.core.service.user;

import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManager;
import com.example.aiexpensetracker.exception.user.EmailAlreadyInUseException;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.user.CreateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateProfilePictureDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UserResponseDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {

    private final RepositoryManager repositoryManager;

    public UserServiceImpl(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Async
    @Override
    public CompletableFuture<UserResponseDTO> createUser(CreateUserDTO createUserDTO) {
        return CompletableFuture.supplyAsync(() -> {
            if (repositoryManager.getUserRepository().existsByEmail(createUserDTO.getEmail())) {
                throw new EmailAlreadyInUseException("Email " + createUserDTO.getEmail() + " is already in use.");
            }

            User user = new User();
            user.setEmail(createUserDTO.getEmail());
            user.setFirstName(createUserDTO.getFirstName());
            user.setLastName(createUserDTO.getLastName());
            user.setProfilePicture(null);

            User savedUser = repositoryManager.getUserRepository().save(user);
            return mapToResponseDTO(savedUser);
        });
    }

    @Async
    @Override
    public CompletableFuture<Optional<UserResponseDTO>> getUserByEmail(String email) {
        return CompletableFuture.supplyAsync(() ->
                repositoryManager.getUserRepository()
                        .findByEmail(email)
                        .map(this::mapToResponseDTO)
        );
    }

    @Async
    @Override
    public CompletableFuture<UserResponseDTO> updateUser(Long id, UpdateUserDTO updateUserDTO) {
        return CompletableFuture.supplyAsync(() -> {
            User user = repositoryManager
                    .getUserRepository()
                    .findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));

            user.setFirstName(updateUserDTO.getFirstName());
            user.setLastName(updateUserDTO.getLastName());

            if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().isEmpty()) {
                // If the email is changing, check for duplicates
                if (!user.getEmail().equals(updateUserDTO.getEmail())
                        && repositoryManager.getUserRepository().existsByEmail(updateUserDTO.getEmail())) {
                    throw new EmailAlreadyInUseException("Email " + updateUserDTO.getEmail() + " is already in use.");
                }
                user.setEmail(updateUserDTO.getEmail());
            }

            User updatedUser = repositoryManager.getUserRepository().save(user);
            return mapToResponseDTO(updatedUser);
        });
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteUser(Long id) {
        return CompletableFuture.runAsync(() -> {
            User user = repositoryManager
                    .getUserRepository()
                    .findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));

            repositoryManager.getUserRepository().delete(user);
        });
    }

    @Async
    @Override
    public CompletableFuture<Void> updateProfilePicture(Long id, UpdateProfilePictureDTO updateProfilePictureDTO) {
        return CompletableFuture.runAsync(() -> {
            User user = repositoryManager
                    .getUserRepository()
                    .findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));

            user.setProfilePicture(updateProfilePictureDTO.getProfilePicture());
            repositoryManager.getUserRepository().save(user);
        });
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setFirstName(user.getFirstName());
        responseDTO.setLastName(user.getLastName());
        responseDTO.setProfilePicture(user.getProfilePicture());
        responseDTO.setCreatedAt(user.getCreatedAt());
        responseDTO.setUpdatedAt(user.getUpdatedAt());
        return responseDTO;
    }
}
