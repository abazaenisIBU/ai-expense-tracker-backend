package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.category.CategoryService;
import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.rest.dto.category.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CategoryControllerTest {

    @Mock
    private ServiceManager serviceManager;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(serviceManager.getCategoryService()).thenReturn(categoryService);
        categoryController = new CategoryController(serviceManager);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testGetAllCategoriesByUser() throws Exception {
        // Arrange
        String userEmail = "john.doe@example.com";

        CategoryResponseDTO category1 = new CategoryResponseDTO();
        category1.setId(1L);
        category1.setName("Groceries");
        category1.setDescription("Supermarket stuff");
        category1.setUserId(101L);
        category1.setCreatedAt(LocalDateTime.now().minusDays(1));
        category1.setUpdatedAt(LocalDateTime.now());

        CategoryResponseDTO category2 = new CategoryResponseDTO();
        category2.setId(2L);
        category2.setName("Utilities");
        category2.setDescription("Monthly bills");
        category2.setUserId(101L);
        category2.setCreatedAt(LocalDateTime.now().minusDays(2));
        category2.setUpdatedAt(LocalDateTime.now());

        List<CategoryResponseDTO> mockCategories = Arrays.asList(category1, category2);

        // Mock the service
        when(categoryService.getAllCategoriesByUser(userEmail))
                .thenReturn(CompletableFuture.completedFuture(mockCategories));

        // Act
        mockMvc.perform(get("/api/categories/user/{email}", userEmail))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testSuggestCategory() throws Exception {
        String email = "john.doe@example.com";
        String description = "Supermarket shopping";

        CategorySuggestionResponseDTO suggestionResponse = new CategorySuggestionResponseDTO();
        suggestionResponse.setCategoryName("Groceries");

        when(categoryService.suggestCategory(description, email))
                .thenReturn(CompletableFuture.completedFuture(suggestionResponse));

        mockMvc.perform(post("/api/categories/user/suggest/{email}", email)
                        .param("description", description))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testCreateCategory() throws Exception {
        String email = "john.doe@example.com";

        // Arrange
        CreateCategoryDTO createCategoryDTO = new CreateCategoryDTO();
        createCategoryDTO.setName("Groceries");
        createCategoryDTO.setDescription("Supermarket stuff");

        CategoryResponseDTO createdCategory = new CategoryResponseDTO();
        createdCategory.setId(1L);
        createdCategory.setName("Groceries");
        createdCategory.setDescription("Supermarket stuff");
        createdCategory.setUserId(101L);
        createdCategory.setCreatedAt(LocalDateTime.now().minusDays(1));
        createdCategory.setUpdatedAt(LocalDateTime.now());

        // Mock the service call with argument matchers
        when(categoryService.createCategory(any(CreateCategoryDTO.class), eq(email)))
                .thenReturn(CompletableFuture.completedFuture(createdCategory));

        // Act
        mockMvc.perform(post("/api/categories/user/{email}", email)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createCategoryDTO)))
                .andReturn();

        verify(categoryService).createCategory(any(CreateCategoryDTO.class), eq(email));
    }

    @Test
    void testUpdateCategory() throws Exception {
        // Arrange
        String email = "john.doe@example.com";
        Long id = 1L;

        UpdateCategoryDTO updateCategoryDTO = new UpdateCategoryDTO();
        updateCategoryDTO.setName("Groceries Updated");
        updateCategoryDTO.setDescription("Updated supermarket stuff");

        CategoryResponseDTO updatedCategory = new CategoryResponseDTO();
        updatedCategory.setId(1L);
        updatedCategory.setName("Groceries Updated");
        updatedCategory.setDescription("Updated supermarket stuff");
        updatedCategory.setUserId(101L);
        updatedCategory.setCreatedAt(LocalDateTime.now().minusDays(1));
        updatedCategory.setUpdatedAt(LocalDateTime.now());

        // Mock the service call with any matchers
        when(categoryService.updateCategory(anyString(), anyLong(), any(UpdateCategoryDTO.class)))
                .thenReturn(CompletableFuture.completedFuture(updatedCategory));

        // Act
        mockMvc.perform(put("/api/categories/user/{email}/{id}", email, id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateCategoryDTO)))
                .andExpect(status().isOk())
                .andReturn();

        verify(categoryService).updateCategory(eq(email), eq(id), any(UpdateCategoryDTO.class));
    }

    @Test
    void testDeleteCategory() throws Exception {
        String email = "john.doe@example.com";
        Long id = 1L;

        when(categoryService.deleteCategory(email, id))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(delete("/api/categories/user/{email}/{id}", email, id))
                .andExpect(status().isOk());
    }
}
