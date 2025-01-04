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

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ServiceManager serviceManager;
    private final MailSender mailSender;

    @Value("${api.key}")
    private String apiKey;

    public ReportController(ServiceManager serviceManager, MailSender mailSender) {
        this.serviceManager = serviceManager;
        this.mailSender = mailSender;
    }

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

    private boolean isValidApiKey(String providedApiKey) {
        return apiKey.equals(providedApiKey);
    }

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
