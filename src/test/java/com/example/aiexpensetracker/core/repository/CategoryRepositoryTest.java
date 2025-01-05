package com.example.aiexpensetracker.core.repository;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CategoryRepositoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Initialize test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
    }

    @Test
    void testFindByUser_CategoriesExist() {
        // Given
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Groceries");
        category1.setUser(testUser);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Utilities");
        category2.setUser(testUser);

        List<Category> mockCategories = Arrays.asList(category1, category2);

        // Mock behavior
        when(categoryRepository.findByUser(testUser)).thenReturn(mockCategories);

        // When
        List<Category> foundCategories = categoryRepository.findByUser(testUser);

        // Then
        assertEquals(2, foundCategories.size(), "Should return 2 categories");
        assertEquals("Groceries", foundCategories.get(0).getName(), "First category name should match");
        assertEquals("Utilities", foundCategories.get(1).getName(), "Second category name should match");
    }

    @Test
    void testFindByUser_NoCategoriesExist() {
        // Mock behavior
        when(categoryRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        // When
        List<Category> foundCategories = categoryRepository.findByUser(testUser);

        // Then
        assertTrue(foundCategories.isEmpty(), "No categories should be found");
    }
}