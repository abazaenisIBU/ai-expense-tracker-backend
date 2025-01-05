package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.core.service.statistics.StatisticsService;
import com.example.aiexpensetracker.rest.dto.statistics.*;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatisticsControllerTest {

    @Mock
    private ServiceManager serviceManager;

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private StatisticsController statisticsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the return of the StatisticsService from the ServiceManager
        when(serviceManager.getStatisticsService()).thenReturn(statisticsService);

        // Initialize the controller with the mocked ServiceManager
        statisticsController = new StatisticsController(serviceManager);

        // Build a standalone MockMvc instance for testing
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();

        // Configure the ObjectMapper for handling date/time serialization properly
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testGetStatistics() throws Exception {
        // Arrange
        String userEmail = "john.doe@example.com";

        // Prepare some mock data for the statistics
        ExpenseDTO expenseDTO = new ExpenseDTO(1L, BigDecimal.valueOf(20.00), LocalDate.now(), "Test Expense");
        CategoryStatisticsDTO categoryStat = new CategoryStatisticsDTO("Groceries", BigDecimal.valueOf(20.00), Collections.singletonList(expenseDTO));
        MonthlyStatisticsDTO monthlyStat = new MonthlyStatisticsDTO("2025-01", BigDecimal.valueOf(20.00), Collections.singletonList(expenseDTO));

        // Create the response DTO that our mock service will return
        StatisticsResponseDTO mockStatistics = new StatisticsResponseDTO(
                Collections.singletonList(categoryStat),
                Collections.singletonList(monthlyStat)
        );

        // Mock the service call
        when(statisticsService.getStatisticsForUser(userEmail))
                .thenReturn(CompletableFuture.completedFuture(mockStatistics));

        // Act
        mockMvc.perform(get("/api/statistics/user/{email}", userEmail))
                .andExpect(status().isOk())
                .andReturn();

        verify(statisticsService).getStatisticsForUser(eq(userEmail));
    }
}
