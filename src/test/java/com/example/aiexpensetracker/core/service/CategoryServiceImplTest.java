package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.api.aiservice.AIService;
import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.category.CategoryRepository;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManager;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import com.example.aiexpensetracker.core.service.category.CategoryServiceImpl;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.category.CategoryResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CategorySuggestionResponseDTO;
import com.example.aiexpensetracker.rest.dto.category.CreateCategoryDTO;
import com.example.aiexpensetracker.rest.dto.category.UpdateCategoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    @Mock
    private RepositoryManager repositoryManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AIService aiService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock RepositoryManager behavior
        when(repositoryManager.getUserRepository()).thenReturn(userRepository);
        when(repositoryManager.getCategoryRepository()).thenReturn(categoryRepository);

        // Initialize test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Groceries");
        testCategory.setDescription("Monthly groceries");
        testCategory.setUser(testUser);
    }

    // --- Test: getAllCategoriesByUser ---
    @Test
    void testGetAllCategoriesByUser_UserExists() throws Exception {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByUser(testUser)).thenReturn(List.of(testCategory));

        CompletableFuture<List<CategoryResponseDTO>> resultFuture = categoryService.getAllCategoriesByUser(testUser.getEmail());
        List<CategoryResponseDTO> result = resultFuture.join();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Groceries", result.get(0).getName());
    }

    @Test
    void testGetAllCategoriesByUser_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        CompletableFuture<List<CategoryResponseDTO>> resultFuture = categoryService.getAllCategoriesByUser("nonexistent@example.com");

        assertThrows(UserNotFoundException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }

    // --- Test: getAllCategoryNamesByUser ---
    @Test
    void testGetAllCategoryNamesByUser_UserExists() throws Exception {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByUser(testUser)).thenReturn(List.of(testCategory));

        CompletableFuture<List<String>> resultFuture = categoryService.getAllCategoryNamesByUser(testUser.getEmail());
        List<String> result = resultFuture.join();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Groceries", result.get(0));
    }

    // --- Test: suggestCategory ---
    @Test
    void testSuggestCategory() throws Exception {
        // Given
        String description = "Buy groceries";
        List<String> existingCategories = List.of("Groceries");

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByUser(testUser)).thenReturn(List.of(testCategory));
        when(aiService.suggestCategory(description, existingCategories))
                .thenReturn(new CategorySuggestionResponseDTO("Groceries", false, 200));

        // When
        CompletableFuture<CategorySuggestionResponseDTO> resultFuture = categoryService.suggestCategory(description, testUser.getEmail());
        CategorySuggestionResponseDTO result = resultFuture.join();

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals("Groceries", result.getCategoryName(), "The suggested category name should be 'Groceries'");
    }

    // --- Test: createCategory ---
    @Test
    void testCreateCategory_UserExists() throws Exception {
        CreateCategoryDTO dto = new CreateCategoryDTO();
        dto.setName("Shopping");
        dto.setDescription("Weekend shopping");

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CompletableFuture<CategoryResponseDTO> resultFuture = categoryService.createCategory(dto, testUser.getEmail());
        CategoryResponseDTO result = resultFuture.join();

        assertNotNull(result);
        assertEquals("Groceries", result.getName());
    }

    @Test
    void testCreateCategory_UserNotFound() {
        CreateCategoryDTO dto = new CreateCategoryDTO();
        dto.setName("Shopping");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        CompletableFuture<CategoryResponseDTO> resultFuture = categoryService.createCategory(dto, "nonexistent@example.com");

        assertThrows(UserNotFoundException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }

    // --- Test: updateCategory ---
    @Test
    void testUpdateCategory_UserAndCategoryExist() throws Exception {
        UpdateCategoryDTO dto = new UpdateCategoryDTO();
        dto.setName("Updated Groceries");
        dto.setDescription("Updated description");

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CompletableFuture<CategoryResponseDTO> resultFuture = categoryService.updateCategory(testUser.getEmail(), testCategory.getId(), dto);
        CategoryResponseDTO result = resultFuture.join();

        assertNotNull(result);
        assertEquals("Updated Groceries", result.getName());
    }

    // --- Test: deleteCategory ---
    @Test
    void testDeleteCategory_UserAndCategoryExist() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));

        CompletableFuture<Void> resultFuture = categoryService.deleteCategory(testUser.getEmail(), testCategory.getId());

        assertDoesNotThrow(resultFuture::join);
        verify(categoryRepository, times(1)).delete(testCategory);
    }
}
