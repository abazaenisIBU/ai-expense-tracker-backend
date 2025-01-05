package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.core.service.user.UserService;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Mock
    private ServiceManager serviceManager;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the ServiceManager to return our mocked UserService
        when(serviceManager.getUserService()).thenReturn(userService);

        // Inject mocks into the controller
        userController = new UserController(serviceManager);

        // Build the standalone setup for MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // Configure ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testCreateUser() throws Exception {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("testuser@example.com");
        createUserDTO.setFirstName("Test");
        createUserDTO.setLastName("User");

        UserResponseDTO createdUser = new UserResponseDTO();
        createdUser.setId(1L);
        createdUser.setEmail("testuser@example.com");
        createdUser.setFirstName("Test");
        createdUser.setLastName("User");
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setUpdatedAt(LocalDateTime.now());

        // Mock service call
        when(userService.createUser(any(CreateUserDTO.class)))
                .thenReturn(CompletableFuture.completedFuture(createdUser));

        // Act
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isOk())  // Because we return ResponseEntity.created(...)
                .andReturn();

        // Verify the service call
        verify(userService).createUser(any(CreateUserDTO.class));
    }

    @Test
    void testUpdateProfilePicture() throws Exception {
        // Arrange
        Long userId = 123L;
        UpdateProfilePictureDTO profilePictureDTO = new UpdateProfilePictureDTO();
        profilePictureDTO.setProfilePicture("profile_pic_url");

        // Mock service call
        when(userService.updateProfilePicture(eq(userId), any(UpdateProfilePictureDTO.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        mockMvc.perform(post("/api/users/{id}/profile-picture", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profilePictureDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the service call
        verify(userService).updateProfilePicture(eq(userId), any(UpdateProfilePictureDTO.class));
    }

    @Test
    void testGetUserByEmail() throws Exception {
        // Arrange
        String userEmail = "testuser@example.com";

        UserResponseDTO existingUser = new UserResponseDTO();
        existingUser.setId(2L);
        existingUser.setEmail(userEmail);
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setCreatedAt(LocalDateTime.now().minusDays(1));
        existingUser.setUpdatedAt(LocalDateTime.now());

        // Mock service call to return an Optional containing the user
        when(userService.getUserByEmail(userEmail))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(existingUser)));

        // Act
        mockMvc.perform(get("/api/users/{email}", userEmail))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the service call
        verify(userService).getUserByEmail(eq(userEmail));
    }

    @Test
    void testGetUserByEmailNotFound() throws Exception {
        // Arrange
        String userEmail = "nonexistent@example.com";

        // Mock service to return an empty Optional
        when(userService.getUserByEmail(userEmail))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // Act & Assert
        mockMvc.perform(get("/api/users/{email}", userEmail))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUser() throws Exception {
        // Arrange
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFirstName("UpdatedFirst");
        updateUserDTO.setLastName("UpdatedLast");
        updateUserDTO.setEmail("updated@example.com");

        UserResponseDTO updatedUser = new UserResponseDTO();
        updatedUser.setId(userId);
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFirstName("UpdatedFirst");
        updatedUser.setLastName("UpdatedLast");
        updatedUser.setCreatedAt(LocalDateTime.now().minusDays(1));
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(userService.updateUser(eq(userId), any(UpdateUserDTO.class)))
                .thenReturn(CompletableFuture.completedFuture(updatedUser));

        // Act
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andReturn();

        verify(userService).updateUser(eq(userId), any(UpdateUserDTO.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        // Arrange
        Long userId = 1L;
        when(userService.deleteUser(userId))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();

        verify(userService).deleteUser(eq(userId));
    }
}
