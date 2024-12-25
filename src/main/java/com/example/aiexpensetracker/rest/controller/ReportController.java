package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.api.mailsender.MailSender;
import com.example.aiexpensetracker.core.service.ServiceManager;
import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

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
    public ResponseEntity<String> generateAndSendMonthlyReports(
            @RequestHeader("X-API-KEY") String providedApiKey) {
        // Validate API key
        if (!apiKey.equals(providedApiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }

        // Get last month's date range
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDate startDate = lastMonth.atDay(1);
        LocalDate endDate = lastMonth.atEndOfMonth();

        // Generate reports for all users
        List<UserReportResponseDTO> reports = serviceManager.getReportService().generateReportsForAllUsers(startDate, endDate);

        // Send reports via email
        try {
            mailSender.sendEmails(reports);
            return ResponseEntity.ok("Monthly reports generated and emails sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send emails: " + e.getMessage());
        }
    }

    @GetMapping("/weekly")
    public ResponseEntity<String> generateAndSendWeeklyReports(
            @RequestHeader("X-API-KEY") String providedApiKey) {
        // Validate API key
        if (!apiKey.equals(providedApiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }

        // Get last week's date range
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        // Generate reports for all users
        List<UserReportResponseDTO> reports = serviceManager.getReportService().generateReportsForAllUsers(startDate, endDate);

        // Send reports via email
        try {
            mailSender.sendEmails(reports);
            return ResponseEntity.ok("Weekly reports generated and emails sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send emails: " + e.getMessage());
        }
    }
}