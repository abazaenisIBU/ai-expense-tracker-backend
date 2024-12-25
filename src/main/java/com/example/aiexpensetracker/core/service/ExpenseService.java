package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.RepositoryManager;
import com.example.aiexpensetracker.core.service.contracts.IExpenseService;
import com.example.aiexpensetracker.rest.dto.expense.*;
import com.example.aiexpensetracker.rest.dto.statistics.CategoryStatisticsDTO;
import com.example.aiexpensetracker.rest.dto.statistics.ExpenseDTO;
import com.example.aiexpensetracker.rest.dto.statistics.MonthlyStatisticsDTO;
import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService implements IExpenseService {

    private final RepositoryManager repositoryManager;

    public ExpenseService(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public List<ExpenseResponseDTO> getAllExpensesByUser(
            String userEmail,
            String sortField,
            String direction
    ) {
        List<Expense> expenses = repositoryManager.getExpenseRepository()
                .findByUserEmail(userEmail);

        if (sortField == null || sortField.isEmpty()) {
            sortField = "date";
        }
        if (direction == null || direction.isEmpty()) {
            direction = "asc";
        }

        switch (sortField.toLowerCase()) {
            case "amount":
                if (direction.equalsIgnoreCase("desc")) {
                    expenses.sort(Comparator.comparing(Expense::getAmount).reversed());
                } else {
                    expenses.sort(Comparator.comparing(Expense::getAmount));
                }
                break;

            case "date":
            default:
                if (direction.equalsIgnoreCase("desc")) {
                    expenses.sort(Comparator.comparing(Expense::getDate).reversed());
                } else {
                    expenses.sort(Comparator.comparing(Expense::getDate));
                }
                break;
        }

        return expenses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    public ExpenseResponseDTO createExpense(CreateExpenseDTO dto, String userEmail) {
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expense.setDescription(dto.getDescription());

        if (dto.getCategoryId() != null) {
            Category cat = repositoryManager.getCategoryRepository()
                    .findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            expense.setCategory(cat);
        }

        Expense saved = repositoryManager.getExpenseRepository().save(expense);
        return mapToResponseDTO(saved);
    }

    public ExpenseResponseDTO updateExpense(String userEmail, Long expenseId, UpdateExpenseDTO dto) {
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = repositoryManager.getExpenseRepository()
                .findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Expense does not belong to this user");
        }

        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expense.setDescription(dto.getDescription());

        if (dto.getCategoryId() != null) {
            Category cat = repositoryManager.getCategoryRepository()
                    .findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            expense.setCategory(cat);
        } else {
            expense.setCategory(null);
        }

        Expense updated = repositoryManager.getExpenseRepository().save(expense);
        return mapToResponseDTO(updated);
    }

    public void deleteExpense(String userEmail, Long expenseId) {
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = repositoryManager.getExpenseRepository()
                .findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Expense does not belong to this user");
        }

        repositoryManager.getExpenseRepository().delete(expense);
    }

    public List<ExpenseResponseDTO> getExpensesByDateRange(String userEmail, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = repositoryManager.getExpenseRepository()
                .findByUserEmailAndDateBetween(userEmail, startDate, endDate);

        return expenses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ExpenseResponseDTO mapToResponseDTO(Expense expense) {
        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setId(expense.getId());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setDescription(expense.getDescription());
        dto.setCreatedAt(expense.getCreatedAt());
        dto.setUpdatedAt(expense.getUpdatedAt());

        if (expense.getCategory() != null) {
            dto.setCategoryId(expense.getCategory().getId());
        }
        dto.setUserId(expense.getUser().getId());
        return dto;
    }

    public StatisticsResponseDTO getStatisticsForUser(String email) {
        // Fetch all expenses for the given user email
        List<Expense> expenses = repositoryManager.getExpenseRepository().findByUserEmail(email);

        // Category Statistics
        Map<String, List<Expense>> expensesByCategory = expenses.stream()
                .collect(Collectors.groupingBy(expense -> expense.getCategory().getName()));

        List<CategoryStatisticsDTO> categoryStatistics = expensesByCategory.entrySet().stream()
                .map(entry -> {
                    String categoryName = entry.getKey();
                    BigDecimal totalAmount = entry.getValue().stream()
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    List<ExpenseDTO> expenseDTOs = entry.getValue().stream()
                            .map(expense -> new ExpenseDTO(
                                    expense.getId(),
                                    expense.getAmount(),
                                    expense.getDate(),
                                    expense.getDescription()))
                            .collect(Collectors.toList());
                    return new CategoryStatisticsDTO(categoryName, totalAmount, expenseDTOs);
                })
                .collect(Collectors.toList());

        // Monthly Statistics
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, List<Expense>> expensesByMonth = expenses.stream()
                .collect(Collectors.groupingBy(expense -> expense.getDate().format(formatter)));

        List<MonthlyStatisticsDTO> monthlyStatistics = expensesByMonth.entrySet().stream()
                .map(entry -> {
                    String monthYear = entry.getKey();
                    BigDecimal totalAmount = entry.getValue().stream()
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    List<ExpenseDTO> expenseDTOs = entry.getValue().stream()
                            .map(expense -> new ExpenseDTO(
                                    expense.getId(),
                                    expense.getAmount(),
                                    expense.getDate(),
                                    expense.getDescription()))
                            .collect(Collectors.toList());
                    return new MonthlyStatisticsDTO(monthYear, totalAmount, expenseDTOs);
                })
                .collect(Collectors.toList());

        // Combine into response
        return new StatisticsResponseDTO(categoryStatistics, monthlyStatistics);
    }
}