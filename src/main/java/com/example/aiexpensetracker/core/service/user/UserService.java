package com.example.aiexpensetracker.core.service.user;

import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.manager.IRepositoryManager;
import com.example.aiexpensetracker.exception.user.EmailAlreadyInUseException;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.user.CreateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateProfilePictureDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UserResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    private final IRepositoryManager repositoryManager;

    public UserService(IRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
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
    }

    @Override
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return repositoryManager.getUserRepository()
                .findByEmail(email)
                .map(this::mapToResponseDTO);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User user = repositoryManager.getUserRepository()
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));

        user.setFirstName(updateUserDTO.getFirstName());
        user.setLastName(updateUserDTO.getLastName());

        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().isEmpty()) {
            if (!user.getEmail().equals(updateUserDTO.getEmail()) &&
                    repositoryManager.getUserRepository().existsByEmail(updateUserDTO.getEmail())) {
                throw new EmailAlreadyInUseException("Email " + updateUserDTO.getEmail() + " is already in use.");
            }
            user.setEmail(updateUserDTO.getEmail());
        }

        User updatedUser = repositoryManager.getUserRepository().save(user);
        return mapToResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = repositoryManager.getUserRepository()
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));

        repositoryManager.getUserRepository().delete(user);
    }

    @Override
    public void updateProfilePicture(Long id, UpdateProfilePictureDTO updateProfilePictureDTO) {
        User user = repositoryManager.getUserRepository()
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));

        user.setProfilePicture(updateProfilePictureDTO.getProfilePicture());
        repositoryManager.getUserRepository().save(user);
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
