package com.example.aiexpensetracker.core.service.contracts;

import com.example.aiexpensetracker.core.model.User;

import java.util.Optional;

public interface IUserService {
    User createUser(User user);

    Optional<User> getUserByEmail(String email);

    User updateUser(Long id, User userDetails);

    void deleteUser(Long id);
}
