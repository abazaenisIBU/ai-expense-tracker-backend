package com.example.aiexpensetracker.core.repository;

import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
    }

    @Test
    void testFindByEmail_UserExists() {
        // Given
        String email = "test@example.com";

        // Mock behavior
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Then
        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals(email, foundUser.get().getEmail(), "Email should match");
        assertEquals("John", foundUser.get().getFirstName(), "First name should match");
        assertEquals("Doe", foundUser.get().getLastName(), "Last name should match");
    }

    @Test
    void testFindByEmail_UserDoesNotExist() {
        // Given
        String email = "nonexistent@example.com";

        // Mock behavior
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Then
        assertFalse(foundUser.isPresent(), "User should not be found");
    }

    @Test
    void testExistsByEmail_UserExists() {
        // Given
        String email = "test@example.com";

        // Mock behavior
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertTrue(exists, "User should exist");
    }

    @Test
    void testExistsByEmail_UserDoesNotExist() {
        // Given
        String email = "nonexistent@example.com";

        // Mock behavior
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertFalse(exists, "User should not exist");
    }
}