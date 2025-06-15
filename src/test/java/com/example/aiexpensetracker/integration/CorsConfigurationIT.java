package com.example.aiexpensetracker.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration-level CORS filter checks (GlobalCorsConfiguration).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CorsConfigurationIT {

    private static final String ALLOWED_ORIGIN = "https://ai-expense-tracker-frontend.onrender.com";
    private static final String BLOCKED_ORIGIN = "http://evil.example";

    @Autowired
    MockMvc mvc;

    @Nested
    class Preflight {

        @Test
        @DisplayName("OPTIONS from allowed origin is accepted and echoes headers")
        void preflightAllowed() throws Exception {
            mvc.perform(options("/api/expenses")
                            .header("Origin", ALLOWED_ORIGIN)
                            .header("Access-Control-Request-Method", HttpMethod.POST))
                    .andExpect(request().asyncNotStarted())             // handled by CorsFilter synchronously
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", ALLOWED_ORIGIN))
                    .andExpect(header().string("Access-Control-Allow-Methods",
                            containsString("POST")));
        }

        @Test
        @DisplayName("OPTIONS from unknown origin is rejected with 403")
        void preflightBlocked() throws Exception {
            mvc.perform(options("/api/expenses")
                            .header("Origin", BLOCKED_ORIGIN)
                            .header("Access-Control-Request-Method", HttpMethod.GET))
                    .andExpect(request().asyncNotStarted())
                    .andExpect(status().isForbidden())
                    .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
        }
    }

    @Nested
    class Simple {

        @Test
        @DisplayName("GET from allowed origin: CORS header present")
        void simpleAllowed() throws Exception {
            MvcResult result = mvc.perform(get("/api/statistics/user/test@test.com")
                            .header("Origin", ALLOWED_ORIGIN))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", ALLOWED_ORIGIN));
        }

        @Test
        @DisplayName("GET from unknown origin: request rejected by CorsFilter")
        void simpleBlocked() throws Exception {
            mvc.perform(get("/api/statistics/user/test@test.com")
                            .header("Origin", BLOCKED_ORIGIN))
                    .andExpect(request().asyncNotStarted())   // filter stops the call
                    .andExpect(status().isForbidden())
                    .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
        }
    }
}
