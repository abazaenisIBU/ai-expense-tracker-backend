package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.api.mailsender.MailSender;
import com.example.aiexpensetracker.core.service.manager.IServiceManager;
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

    private final IServiceManager serviceManager;
    private final MailSender mailSender;

    @Value("${api.key}")
    private String apiKey;

    public ReportController(IServiceManager serviceManager, MailSender mailSender) {
        this.serviceManager = serviceManager;
        this.mailSender = mailSender;
    }

    @GetMapping("/monthly")
    public ResponseEntity<String> generateAndSendMonthlyReports(@RequestHeader("X-API-KEY") String providedApiKey) {
        if (!isValidApiKey(providedApiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }

        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDate startDate = lastMonth.atDay(1);
        LocalDate endDate = lastMonth.atEndOfMonth();

        return generateAndSendReports(startDate, endDate, "Monthly reports generated and emails sent successfully.");
    }

    @GetMapping("/weekly")
    public ResponseEntity<String> generateAndSendWeeklyReports(@RequestHeader("X-API-KEY") String providedApiKey) {
        if (!isValidApiKey(providedApiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        return generateAndSendReports(startDate, endDate, "Weekly reports generated and emails sent successfully.");
    }

    private boolean isValidApiKey(String providedApiKey) {
        return apiKey.equals(providedApiKey);
    }

    private ResponseEntity<String> generateAndSendReports(LocalDate startDate, LocalDate endDate, String successMessage) {
        List<UserReportResponseDTO> reports = serviceManager.getReportService().generateReportsForAllUsers(startDate, endDate);

        try {
            mailSender.sendEmails(reports);
            return ResponseEntity.ok(successMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send emails: " + e.getMessage());
        }
    }
}