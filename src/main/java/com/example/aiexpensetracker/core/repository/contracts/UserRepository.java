package com.example.aiexpensetracker.core.repository.contracts;

import com.example.aiexpensetracker.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
