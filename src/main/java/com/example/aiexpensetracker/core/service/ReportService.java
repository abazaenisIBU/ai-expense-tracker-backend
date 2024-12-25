package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.RepositoryManager;
import com.example.aiexpensetracker.rest.dto.report.CategoryTotalDTO;
import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;
import com.example.aiexpensetracker.rest.dto.statistics.ExpenseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final RepositoryManager repositoryManager;

    public ReportService(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public List<UserReportResponseDTO> generateReportsForAllUsers(LocalDate startDate, LocalDate endDate) {
        // Fetch all unique user emails
        List<User> users = repositoryManager.getUserRepository().findAll();

        List<UserReportResponseDTO> userReports = new ArrayList<>();

        for (User user : users) {
            String email = user.getEmail();

            // Fetch expenses for the user within the given date range
            List<Expense> expenses = repositoryManager.getExpenseRepository()
                    .findByUserEmailAndDateBetween(email, startDate, endDate);

            // Calculate total amount
            BigDecimal totalAmount = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate total amount by category
            Map<String, BigDecimal> amountByCategory = expenses.stream()
                    .collect(Collectors.groupingBy(
                            expense -> expense.getCategory().getName(),
                            Collectors.mapping(
                                    Expense::getAmount,
                                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            List<CategoryTotalDTO> categoryTotals = amountByCategory.entrySet().stream()
                    .map(entry -> new CategoryTotalDTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            // Convert expenses to DTOs
            List<ExpenseDTO> expenseDTOs = expenses.stream()
                    .map(expense -> new ExpenseDTO(
                            expense.getId(),
                            expense.getAmount(),
                            expense.getDate(),
                            expense.getDescription()))
                    .collect(Collectors.toList());

            // Create the user's report
            UserReportResponseDTO userReport = new UserReportResponseDTO(email, expenseDTOs, totalAmount, categoryTotals);
            userReports.add(userReport);
        }

        return userReports;
    }
}