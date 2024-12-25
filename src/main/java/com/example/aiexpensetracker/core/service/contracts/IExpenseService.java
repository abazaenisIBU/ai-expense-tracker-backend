package com.example.aiexpensetracker.core.service.contracts;

import com.example.aiexpensetracker.rest.dto.expense.*;
import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface IExpenseService {
    List<ExpenseResponseDTO> getAllExpensesByUser(String userEmail, String sortBy, String direction);

    ExpenseResponseDTO createExpense(CreateExpenseDTO dto, String userEmail);

    ExpenseResponseDTO updateExpense(String userEmail, Long expenseId, UpdateExpenseDTO dto);

    void deleteExpense(String userEmail, Long expenseId);

    List<ExpenseResponseDTO> getExpensesByDateRange(String userEmail, LocalDate startDate, LocalDate endDate);

    StatisticsResponseDTO getStatisticsForUser(String email);
}
