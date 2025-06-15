package com.example.aiexpensetracker.integration;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.category.CategoryRepository;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end tests for CategoryController
 * (real web layer • real DB – H2 • async dispatch).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerIT {

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired CategoryRepository categories;

    private static final String EMAIL = "enis@test.com";
    private Long foodId;

    @BeforeEach
    void setUp() {
        categories.deleteAll();
        users.deleteAll();

        User u = new User();
        u.setEmail(EMAIL);
        u.setFirstName("Enis");
        u.setLastName("Abaza");
        users.save(u);

        Category food = new Category();
        food.setName("Food");
        food.setUser(u);
        foodId = categories.save(food).getId();
    }

    @Test
    @DisplayName("GET → user’s categories")
    void listCategories() throws Exception {
        MvcResult start = mvc.perform(get("/api/categories/user/{email}", EMAIL))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(start))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Food"));
    }

    @Test
    @DisplayName("POST create → appears in subsequent GET")
    void createCategory_thenVisibleInList() throws Exception {
        String body = """
            {
              "name": "Travel"
            }""";

        MvcResult post = mvc.perform(post("/api/categories/user/{email}", EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(post))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Travel"));

        MvcResult list = mvc.perform(get("/api/categories/user/{email}", EMAIL))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(list))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name",
                        containsInAnyOrder("Food", "Travel")));
    }

    @Test
    @DisplayName("PUT update → new name returned")
    void updateCategory() throws Exception {
        String body = """
            {
              "name": "Groceries"
            }""";

        MvcResult put = mvc.perform(put("/api/categories/user/{email}/{id}",
                        EMAIL, foodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(put))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Groceries"));
    }

    @Test
    @DisplayName("DELETE removes category")
    void deleteCategory() throws Exception {
        MvcResult del = mvc.perform(delete("/api/categories/user/{email}/{id}",
                        EMAIL, foodId))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(del)).andExpect(status().isNoContent());

        MvcResult list = mvc.perform(get("/api/categories/user/{email}", EMAIL))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(list))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST with empty body → 400 Bad Request")
    void createCategory_invalidBody() throws Exception {
        mvc.perform(post("/api/categories/user/{email}", EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(request().asyncNotStarted())
                .andExpect(status().isBadRequest());
    }

    // TODO (Bug found): Fix 500 internal server error (will be fixed in chapter 4)
    @Test
    @DisplayName("POST suggestCategory without description → 400 Bad Request")
    void suggestCategory_missingDescription() throws Exception {
        mvc.perform(post("/api/categories/user/suggest/{email}", EMAIL))
                .andExpect(request().asyncNotStarted())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT update with empty body → 400 Bad Request")
    void updateCategory_invalidBody() throws Exception {
        mvc.perform(put("/api/categories/user/{email}/{id}", EMAIL, foodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(request().asyncNotStarted())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE unknown id → 404 Not Found")
    void deleteCategory_notFound() throws Exception {
        long unknown = foodId + 999;

        MvcResult del = mvc.perform(delete("/api/categories/user/{email}/{id}",
                        EMAIL, unknown))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(del))
                .andExpect(status().isNotFound());
    }
}
