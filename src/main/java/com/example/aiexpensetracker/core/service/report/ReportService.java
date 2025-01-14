package com.example.aiexpensetracker.core.service.report;

import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for generating reports for users in the AI Expense Tracker application.
 * Provides an asynchronous method to generate financial reports for all users within a specified date range.
 */
public interface ReportService {

    /**
     * Generates expense reports for all users within a specified date range.
     * The report includes total expenses, categorized amounts, and a detailed breakdown of expenses for each user.
     *
     * @param startDate the start date of the reporting period.
     * @param endDate   the end date of the reporting period.
     * @return a CompletableFuture containing a list of UserReportResponseDTO objects, each representing a user's report.
     */
    CompletableFuture<List<UserReportResponseDTO>> generateReportsForAllUsers(LocalDate startDate, LocalDate endDate);
}
