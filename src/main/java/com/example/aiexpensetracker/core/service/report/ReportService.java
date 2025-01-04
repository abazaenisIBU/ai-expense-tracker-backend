package com.example.aiexpensetracker.core.service.report;

import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ReportService {
    CompletableFuture<List<UserReportResponseDTO>> generateReportsForAllUsers(LocalDate startDate, LocalDate endDate);
}
