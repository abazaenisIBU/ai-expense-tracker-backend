package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManager;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import com.example.aiexpensetracker.core.service.user.UserServiceImpl;
import com.example.aiexpensetracker.exception.user.EmailAlreadyInUseException;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.user.CreateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateProfilePictureDTO;
import com.example.aiexpensetracker.rest.dto.user.UpdateUserDTO;
import com.example.aiexpensetracker.rest.dto.user.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private RepositoryManager repositoryManager;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(repositoryManager.getUserRepository()).thenReturn(userRepository);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
    }

    // --- Test: createUser ---
    @Test
    void testCreateUser_Success() throws Exception {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("newuser@example.com");
        dto.setFirstName("Jane");
        dto.setLastName("Doe");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        CompletableFuture<UserResponseDTO> resultFuture = userService.createUser(dto);
        UserResponseDTO result = resultFuture.get();

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testCreateUser_EmailAlreadyInUse() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("test@example.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        CompletableFuture<UserResponseDTO> resultFuture = userService.createUser(dto);

        assertThrows(EmailAlreadyInUseException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }

    // --- Test: getUserByEmail ---
    @Test
    void testGetUserByEmail_UserExists() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        CompletableFuture<Optional<UserResponseDTO>> resultFuture = userService.getUserByEmail("test@example.com");
        Optional<UserResponseDTO> result = resultFuture.get();

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testGetUserByEmail_UserNotFound() throws Exception {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        CompletableFuture<Optional<UserResponseDTO>> resultFuture = userService.getUserByEmail("unknown@example.com");
        Optional<UserResponseDTO> result = resultFuture.get();

        assertFalse(result.isPresent());
    }

    // --- Test: updateUser ---
    @Test
    void testUpdateUser_Success() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        CompletableFuture<UserResponseDTO> resultFuture = userService.updateUser(1L, dto);
        UserResponseDTO result = resultFuture.get();

        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setFirstName("Jane");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        CompletableFuture<UserResponseDTO> resultFuture = userService.updateUser(99L, dto);

        assertThrows(UserNotFoundException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }

    @Test
    void testUpdateUser_EmailAlreadyInUse() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setEmail("taken@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        CompletableFuture<UserResponseDTO> resultFuture = userService.updateUser(1L, dto);

        assertThrows(EmailAlreadyInUseException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }

    // --- Test: deleteUser ---
    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        CompletableFuture<Void> resultFuture = userService.deleteUser(1L);

        assertDoesNotThrow(resultFuture::join);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        CompletableFuture<Void> resultFuture = userService.deleteUser(99L);

        assertThrows(UserNotFoundException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }

    // --- Test: updateProfilePicture ---
    @Test
    void testUpdateProfilePicture_Success() {
        UpdateProfilePictureDTO dto = new UpdateProfilePictureDTO();
        dto.setProfilePicture("newProfilePic.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        CompletableFuture<Void> resultFuture = userService.updateProfilePicture(1L, dto);

        assertDoesNotThrow(resultFuture::join);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateProfilePicture_UserNotFound() {
        UpdateProfilePictureDTO dto = new UpdateProfilePictureDTO();
        dto.setProfilePicture("newProfilePic.jpg");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        CompletableFuture<Void> resultFuture = userService.updateProfilePicture(99L, dto);

        assertThrows(UserNotFoundException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }
}
