package com.example.aiexpensetracker.integration;

import com.example.aiexpensetracker.core.service.statistics.StatisticsService;
import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class StatisticsControllerIT {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    StatisticsService statisticsService;

    private static final String EMAIL = "stats@test.com";

    @Test
    @DisplayName("GET /api/statistics/user/{email} returns user statistics")
    void getStatistics_returnsDto() throws Exception {
        StatisticsResponseDTO dto = new StatisticsResponseDTO(
                List.of(),   // categoryStatistics
                List.of()    // monthlyStatistics
        );

        Mockito.when(statisticsService.getStatisticsForUser(EMAIL))
                .thenReturn(CompletableFuture.completedFuture(dto));

        MvcResult start = mvc.perform(get("/api/statistics/user/{email}", EMAIL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(start))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.categoryStatistics", hasSize(0)))
                .andExpect(jsonPath("$.monthlyStatistics",  hasSize(0)));
    }

    @Test
    @DisplayName("service throws ⇒ 500")
    void serviceFailure_500() throws Exception {
        CompletableFuture<StatisticsResponseDTO> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("boom"));

        Mockito.when(statisticsService.getStatisticsForUser(EMAIL)).thenReturn(failed);

        MvcResult start = mvc.perform(get("/api/statistics/user/{email}", EMAIL))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(start))
                .andExpect(status().isInternalServerError());
    }

    // TODO (Bug found): Return 400 instead of 500 when email is malformed
    @Test
    @DisplayName("malformed e-mail ⇒ 400")
    void badEmail_400() throws Exception {
        // path variable that clearly violates a typical email regexp
        mvc.perform(get("/api/statistics/user/{email}", "not-an-email"))
                .andExpect(status().isBadRequest());
    }

    // TODO (Bug found): Return 400 instead of 500 when email is null
    @Test
    @DisplayName("malformed e-mail ⇒ 400")
    void nullEmail_400() throws Exception {
        // path variable that clearly violates a typical email regexp
        mvc.perform(get("/api/statistics/user/{email}", null))
                .andExpect(status().isBadRequest());
    }
}
