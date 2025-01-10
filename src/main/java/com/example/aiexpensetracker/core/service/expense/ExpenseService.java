package com.example.aiexpensetracker.core.service.expense;

import com.example.aiexpensetracker.rest.dto.expense.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ExpenseService {
    CompletableFuture<List<ExpenseResponseDTO>> getAllExpensesByUser(String userEmail, String sortBy, String direction, String filterColumn, String filterValue);

    CompletableFuture<ExpenseResponseDTO> createExpense(CreateExpenseDTO dto, String userEmail);

    CompletableFuture<ExpenseResponseDTO> updateExpense(String userEmail, Long expenseId, UpdateExpenseDTO dto);

    CompletableFuture<Void> deleteExpense(String userEmail, Long expenseId);
}
