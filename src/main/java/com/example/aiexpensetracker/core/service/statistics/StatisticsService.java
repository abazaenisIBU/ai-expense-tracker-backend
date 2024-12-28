package com.example.aiexpensetracker.core.service.statistics;

import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.repository.manager.IRepositoryManager;
import com.example.aiexpensetracker.rest.dto.statistics.CategoryStatisticsDTO;
import com.example.aiexpensetracker.rest.dto.statistics.ExpenseDTO;
import com.example.aiexpensetracker.rest.dto.statistics.MonthlyStatisticsDTO;
import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService implements IStatisticsService {

    private final IRepositoryManager repositoryManager;

    public StatisticsService(IRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public StatisticsResponseDTO getStatisticsForUser(String email) {
        List<Expense> expenses = repositoryManager.getExpenseRepository().findByUserEmail(email);

        List<CategoryStatisticsDTO> categoryStatistics = calculateStatisticsByCategory(expenses);
        List<MonthlyStatisticsDTO> monthlyStatistics = calculateStatisticsByMonth(expenses);

        return new StatisticsResponseDTO(categoryStatistics, monthlyStatistics);
    }

    private List<CategoryStatisticsDTO> calculateStatisticsByCategory(List<Expense> expenses) {
        return expenses.stream()
                .collect(Collectors.groupingBy(expense -> expense.getCategory().getName()))
                .entrySet()
                .stream()
                .map(entry -> {
                    String categoryName = entry.getKey();
                    List<Expense> categoryExpenses = entry.getValue();
                    BigDecimal totalAmount = calculateTotalAmount(categoryExpenses);
                    List<ExpenseDTO> expenseDTOs = mapToExpenseDTOs(categoryExpenses);
                    return new CategoryStatisticsDTO(categoryName, totalAmount, expenseDTOs);
                })
                .collect(Collectors.toList());
    }

    private List<MonthlyStatisticsDTO> calculateStatisticsByMonth(List<Expense> expenses) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return expenses.stream()
                .collect(Collectors.groupingBy(expense -> expense.getDate().format(formatter)))
                .entrySet()
                .stream()
                .map(entry -> {
                    String monthYear = entry.getKey();
                    List<Expense> monthlyExpenses = entry.getValue();
                    BigDecimal totalAmount = calculateTotalAmount(monthlyExpenses);
                    List<ExpenseDTO> expenseDTOs = mapToExpenseDTOs(monthlyExpenses);
                    return new MonthlyStatisticsDTO(monthYear, totalAmount, expenseDTOs);
                })
                .collect(Collectors.toList());
    }

    private BigDecimal calculateTotalAmount(List<Expense> expenses) {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ExpenseDTO> mapToExpenseDTOs(List<Expense> expenses) {
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

