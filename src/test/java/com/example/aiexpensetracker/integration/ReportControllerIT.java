package com.example.aiexpensetracker.integration;

import com.example.aiexpensetracker.core.api.mailsender.MailSender;
import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.core.service.report.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@code ReportController}.
 * We keep the full web stack and only stub:
 *   • {@link MailSender} (external SMTP)
 *   • {@link ReportService} (long-running aggregation)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "api.key=TEST_KEY")
class ReportControllerIT {

    @Autowired MockMvc mvc;

    @MockitoBean
    MailSender mailSender;

    @MockitoBean
    ReportService reportService;

    @MockitoBean
    ServiceManager serviceManager;

    private static final String GOOD_KEY = "TEST_KEY";
    private static final String BAD_KEY  = "WRONG";

    private void stubReportGeneration() {
        Mockito.when(reportService.generateReportsForAllUsers(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));
        Mockito.when(serviceManager.getReportService()).thenReturn(reportService);
    }

    @Nested
    @DisplayName("/api/reports/monthly")
    class Monthly {

        @Test
        @DisplayName("200 OK with valid API key")
        void monthly_ok() throws Exception {
            stubReportGeneration();

            MvcResult start = mvc.perform(get("/api/reports/monthly")
                            .header("X-API-KEY", GOOD_KEY))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                    .andExpect(content().string(
                            org.hamcrest.Matchers.containsString("Monthly reports generated")));

            verify(mailSender).sendEmails(Collections.emptyList());
        }

        @Test
        @DisplayName("403 with wrong key")
        void monthly_badKey() throws Exception {
            MvcResult start = mvc.perform(get("/api/reports/monthly")
                            .header("X-API-KEY", BAD_KEY))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid API Key")));
        }
    }

    @Nested
    @DisplayName("/api/reports/weekly")
    class Weekly {

        @Test
        @DisplayName("200 OK with valid API key")
        void weekly_ok() throws Exception {
            stubReportGeneration();

            MvcResult start = mvc.perform(get("/api/reports/weekly")
                            .header("X-API-KEY", GOOD_KEY))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isOk())
                    .andExpect(content().string(
                            org.hamcrest.Matchers.containsString("Weekly reports generated")));

            verify(mailSender).sendEmails(Collections.emptyList());
        }

        @Test
        @DisplayName("403 with wrong key")
        void weekly_badKey() throws Exception {
            MvcResult start = mvc.perform(get("/api/reports/weekly")
                            .header("X-API-KEY", BAD_KEY))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid API Key")));
        }

        // TODO (Bug found): Return 400 instead of 500 when header is non-existent
        // TODO (Bug found): Expeses in recieved email are filtered correctly by week, but the title is "Monthly Expenses" instead of "Weekly Expenses"
        @Test
        @DisplayName("403 when API key header is absent")
        void weekly_noKeyHeader() throws Exception {

            MvcResult start = mvc.perform(get("/api/reports/weekly"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(
                            org.hamcrest.Matchers.containsString("Invalid API Key")));
        }
    }

    @Nested
    @DisplayName("/api/reports/monthly ­– extra edge-cases")
    class MonthlyExtra {

        // TODO (Bug found): Return 400 instead of 500 when header is non-existent
        @Test
        @DisplayName("403 when API key header is absent")
        void monthly_noKeyHeader() throws Exception {

            MvcResult start = mvc.perform(get("/api/reports/monthly"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(
                            org.hamcrest.Matchers.containsString("Invalid API Key")));
        }

        @Test
        @DisplayName("500 when MailSender throws during dispatch")
        void monthly_mailFailure() throws Exception {
            stubReportGeneration();

            // make mailSender.throw RuntimeException
            doThrow(new RuntimeException("SMTP down"))
                    .when(mailSender).sendEmails(anyList());

            MvcResult start = mvc.perform(get("/api/reports/monthly")
                            .header("X-API-KEY", GOOD_KEY))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mvc.perform(asyncDispatch(start))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(
                            org.hamcrest.Matchers.containsString("Failed to send emails")));
        }
    }
}
