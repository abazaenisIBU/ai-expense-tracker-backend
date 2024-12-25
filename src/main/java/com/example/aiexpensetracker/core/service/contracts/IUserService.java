package com.example.aiexpensetracker.core.service.contracts;

import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.rest.dto.user.CreateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateProfilePictureDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UserResponseDTO;

import java.util.Optional;

public interface IUserService {
    UserResponseDTO createUser(CreateUserDTO createUserDTO);

    Optional<UserResponseDTO> getUserByEmail(String email);

    UserResponseDTO updateUser(Long id, UpdateUserDTO updateUserDTO);

    void updateProfilePicture(Long id, UpdateProfilePictureDTO updateProfilePictureDTO);

    void deleteUser(Long id);
}
