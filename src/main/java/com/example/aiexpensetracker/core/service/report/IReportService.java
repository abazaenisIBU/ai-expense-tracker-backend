package com.example.aiexpensetracker.core.service.report;

import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface IReportService {
    List<UserReportResponseDTO> generateReportsForAllUsers(LocalDate startDate, LocalDate endDate);
}
