package com.example.aiexpensetracker.integration;

import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ExpenseControllerIT {

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;

    @BeforeEach
    void seedDb() {
        User u = new User();
        u.setEmail("enis@test.com");
        u.setFirstName("Enis");
        u.setLastName("Abaza");
        users.save(u);
    }

    @Test
    void createExpense_thenListIt() throws Exception {
        String json = """
            {
              "amount"     : 12.34,
              "date"       : "2025-06-15",
              "description": "Pizza"
            }""";

        MvcResult postInit = mvc.perform(
                        post("/api/expenses/user/{email}", "enis@test.com")
                                .contentType("application/json")
                                .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(postInit))
                .andExpect(status().isCreated());

        MvcResult getInit = mvc.perform(
                        get("/api/expenses/user/{email}", "enis@test.com"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(getInit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Pizza"))
                .andExpect(jsonPath("$[0].amount").value(12.34));
    }

    @Test
    void createExpense_missingAmount_returns400() throws Exception {
        String badJson = """
            {
              "date"       : "2025-06-15",
              "description": "No amount!"
            }""";

        mvc.perform(post("/api/expenses/user/{email}", "enis@test.com")
                        .contentType("application/json")
                        .content(badJson))
                .andExpect(request().asyncNotStarted())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateExpense_notFound_returns404() throws Exception {
        String body = """
            {
              "amount"     : 20.00,
              "date"       : "2025-06-16",
              "description": "Update attempt"
            }""";

        long unknownId = 999L;

        MvcResult putInit = mvc.perform(put("/api/expenses/user/{email}/{id}",
                        "enis@test.com", unknownId)
                        .contentType("application/json")
                        .content(body))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(putInit))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExpense_notFound_returns404() throws Exception {
        long unknownId = 999L;

        MvcResult delInit = mvc.perform(delete("/api/expenses/user/{email}/{id}",
                        "enis@test.com", unknownId))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(delInit))
                .andExpect(status().isNotFound());
    }
}
