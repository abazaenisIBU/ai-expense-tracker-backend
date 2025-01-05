package com.example.aiexpensetracker.core.service.report;

import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManager;
import com.example.aiexpensetracker.rest.dto.report.CategoryTotalDTO;
import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;
import com.example.aiexpensetracker.rest.dto.statistics.ExpenseDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final RepositoryManager repositoryManager;

    public ReportServiceImpl(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Async
    @Override
    public CompletableFuture<List<UserReportResponseDTO>> generateReportsForAllUsers(
            LocalDate startDate,
            LocalDate endDate
    ) {
        return CompletableFuture.supplyAsync(() -> repositoryManager.getUserRepository()
                .findAll()
                .stream()
                .map(user -> generateReportForUser(user, startDate, endDate))
                .collect(Collectors.toList()));
    }

    private UserReportResponseDTO generateReportForUser(User user, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = repositoryManager.getExpenseRepository()
                .findByUserEmailAndDateBetween(user.getEmail(), startDate, endDate);

        BigDecimal totalAmount = calculateTotalAmount(expenses);
        List<CategoryTotalDTO> categoryTotals = calculateCategoryTotals(expenses);
        List<ExpenseDTO> expenseDTOs = mapToExpenseDTOs(expenses);

        return new UserReportResponseDTO(
                user.getEmail(),
                expenseDTOs,
                totalAmount,
                categoryTotals
        );
    }

    public BigDecimal calculateTotalAmount(List<Expense> expenses) {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CategoryTotalDTO> calculateCategoryTotals(List<Expense> expenses) {
        Map<String, BigDecimal> amountByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getCategory().getName(),
                        Collectors.mapping(
                                Expense::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )));

        return amountByCategory.entrySet()
                .stream()
                .map(entry -> new CategoryTotalDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<ExpenseDTO> mapToExpenseDTOs(List<Expense> expenses) {
        return expenses.stream()
                .map(expense -> new ExpenseDTO(
                        expense.getId(),
                        expense.getAmount(),
                        expense.getDate(),
                        expense.getDescription()
                ))
                .collect(Collectors.toList());
    }
}
