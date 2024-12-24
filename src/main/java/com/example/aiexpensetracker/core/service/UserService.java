package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.RepositoryManager;
import com.example.aiexpensetracker.core.service.contracts.IUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    private final RepositoryManager repositoryManager;

    public UserService(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public User createUser(User user) {
        return repositoryManager.getUserRepository().save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(repositoryManager.getUserRepository().findByEmail(email));
    }

    public User updateUser(Long id, User userDetails) {
        User user = repositoryManager.getUserRepository().findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setProfilePicture(userDetails.getProfilePicture());
        return repositoryManager.getUserRepository().save(user);
    }

    public void deleteUser(Long id) {
        repositoryManager.getUserRepository().deleteById(id);
    }
}
