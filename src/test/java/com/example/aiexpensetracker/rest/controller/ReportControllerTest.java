package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.api.mailsender.MailSender;
import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.core.service.report.ReportService;
import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReportControllerTest {

    @Mock
    private ServiceManager serviceManager;

    @Mock
    private MailSender mailSender;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(serviceManager.getReportService()).thenReturn(reportService);
        reportController = new ReportController(serviceManager, mailSender);
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();

        // Set the apiKey using reflection
        ReflectionTestUtils.setField(reportController, "apiKey", "TEST_API_KEY");
    }

    @Test
    void testGenerateAndSendMonthlyReports_withValidApiKey() throws Exception {
        // Arrange: Mock the ReportService to return a completed CompletableFuture with a list of UserReportResponseDTO
        UserReportResponseDTO mockReport = new UserReportResponseDTO(
                "john.doe@example.com",
                Collections.emptyList(),
                BigDecimal.valueOf(100),
                Collections.emptyList()
        );

        when(reportService.generateReportsForAllUsers(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(mockReport)));

        // Act & Assert
        mockMvc.perform(get("/api/reports/monthly")
                        .header("X-API-KEY", "TEST_API_KEY"))
                .andExpect(status().isOk());
    }

    @Test
    void testGenerateAndSendWeeklyReports_withValidApiKey() throws Exception {
        // Arrange: Mock the ReportService to return a completed CompletableFuture with a list of UserReportResponseDTO
        UserReportResponseDTO mockReport = new UserReportResponseDTO(
                "jane.doe@example.com",
                Collections.emptyList(),
                BigDecimal.valueOf(200),
                Collections.emptyList()
        );

        when(reportService.generateReportsForAllUsers(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(mockReport)));

        // Act & Assert
        mockMvc.perform(get("/api/reports/weekly")
                        .header("X-API-KEY", "TEST_API_KEY"))
                .andExpect(status().isOk());
    }
}
