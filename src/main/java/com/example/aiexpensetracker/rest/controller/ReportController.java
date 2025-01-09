package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.api.mailsender.MailSender;
import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for generating and sending reports in the AI Expense Tracker application.
 * Provides endpoints to generate and send both monthly and weekly reports via email.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ServiceManager serviceManager;
    private final MailSender mailSender;

    @Value("${api.key}")
    private String apiKey;

    /**
     * Constructs a new ReportController with the provided ServiceManager and MailSender.
     *
     * @param serviceManager the ServiceManager instance that provides access to various services.
     * @param mailSender     the MailSender instance responsible for sending emails.
     */
    public ReportController(ServiceManager serviceManager, MailSender mailSender) {
        this.serviceManager = serviceManager;
        this.mailSender = mailSender;
    }

    /**
     * Generates and sends monthly reports for all users.
     * Requires a valid API key provided in the request header.
     *
     * @param providedApiKey the API key provided in the "X-API-KEY" header.
     * @return a CompletableFuture containing a ResponseEntity with a success message or an error message.
     */
    @GetMapping("/monthly")
    public CompletableFuture<ResponseEntity<String>> generateAndSendMonthlyReports(
            @RequestHeader("X-API-KEY") String providedApiKey
    ) {
        if (!isValidApiKey(providedApiKey)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key"));
        }

        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDate startDate = lastMonth.atDay(1);
        LocalDate endDate = lastMonth.atEndOfMonth();

        return generateAndSendReports(startDate, endDate, "Monthly reports generated and emails sent successfully.");
    }

    /**
     * Generates and sends weekly reports for all users.
     * Requires a valid API key provided in the request header.
     *
     * @param providedApiKey the API key provided in the "X-API-KEY" header.
     * @return a CompletableFuture containing a ResponseEntity with a success message or an error message.
     */
    @GetMapping("/weekly")
    public CompletableFuture<ResponseEntity<String>> generateAndSendWeeklyReports(
            @RequestHeader("X-API-KEY") String providedApiKey
    ) {
        if (!isValidApiKey(providedApiKey)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key"));
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        return generateAndSendReports(startDate, endDate, "Weekly reports generated and emails sent successfully.");
    }

    /**
     * Validates the provided API key against the configured API key.
     *
     * @param providedApiKey the API key provided in the request.
     * @return true if the API key is valid, false otherwise.
     */
    private boolean isValidApiKey(String providedApiKey) {
        return apiKey.equals(providedApiKey);
    }

    /**
     * Generates reports for all users within the specified date range and sends them via email.
     *
     * @param startDate     the start date of the report period.
     * @param endDate       the end date of the report period.
     * @param successMessage the success message to return if the emails are sent successfully.
     * @return a CompletableFuture containing a ResponseEntity with a success message or an error message.
     */
    private CompletableFuture<ResponseEntity<String>> generateAndSendReports(
            LocalDate startDate,
            LocalDate endDate,
            String successMessage
    ) {
        return serviceManager.getReportService()
                .generateReportsForAllUsers(startDate, endDate)
                .thenApply(reports -> {
                    try {
                        mailSender.sendEmails(reports);
                        return ResponseEntity.ok(successMessage);
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to send emails: " + e.getMessage());
                    }
                });
    }
}
