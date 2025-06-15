package com.example.aiexpensetracker.integration;

import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;

    @BeforeEach
    void clean() {
        users.deleteAll();
    }

    @Nested
    class Create {

        @Test
        @DisplayName("POST /api/users creates user → 201 + Location")
        void createUser() throws Exception {
            String json = """
                {
                  "email": "john@test.com",
                  "firstName": "John",
                  "lastName":  "Doe"
                }""";

            MvcResult start = mvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location",
                            containsString("/api/users/john@test.com")))
                    .andExpect(jsonPath("$.email").value("john@test.com"))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.id").isNumber());
        }
    }

    @Nested
    class Crud {

        Long id;

        @BeforeEach
        void seed() {
            User u = new User();
            u.setEmail("alice@test.com");
            u.setFirstName("Alice");
            u.setLastName("Smith");
            id = users.save(u).getId();
        }

        @Test
        void getUserByEmail() throws Exception {
            MvcResult start = mvc.perform(get("/api/users/{email}", "alice@test.com"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("alice@test.com"))
                    .andExpect(jsonPath("$.firstName").value("Alice"))
                    .andExpect(jsonPath("$.lastName").value("Smith"))
                    .andExpect(jsonPath("$.profilePicture").doesNotExist());
        }

        @Test
        void updateUser() throws Exception {
            String json = """
                {
                  "email": "alice@test.com",
                  "firstName": "Alicia",
                  "lastName":  "Johnson"
                }""";

            MvcResult start = mvc.perform(put("/api/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Alicia"))
                    .andExpect(jsonPath("$.lastName").value("Johnson"));
        }

        @Test
        void updateProfilePicture() throws Exception {
            String json = """
                { "profilePicture": "avatar.png" }""";

            MvcResult picStart = mvc.perform(
                            post("/api/users/{id}/profile-picture", id)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(picStart))
                    .andExpect(status().isOk());

            // verify
            MvcResult getStart = mvc.perform(get("/api/users/{email}", "alice@test.com"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(getStart))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.profilePicture").value("avatar.png"));
        }

        @Test
        void deleteUser() throws Exception {
            MvcResult delStart = mvc.perform(delete("/api/users/{id}", id))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(delStart))
                    .andExpect(status().isNoContent());

            // lookup should now 404
            MvcResult lookup = mvc.perform(get("/api/users/{email}", "alice@test.com"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(lookup))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("PUT unknown id → 404")
        void updateUser_notFound() throws Exception {
            String json = """
                {
                  "email": "ghost@test.com",
                  "firstName": "Ghost",
                  "lastName":  "User"
                }""";

            long unknown = id + 999;

            MvcResult start = mvc.perform(put("/api/users/{id}", unknown)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isNotFound());
        }
    }
}
