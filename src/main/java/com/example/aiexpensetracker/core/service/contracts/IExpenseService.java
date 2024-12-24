package com.example.aiexpensetracker.core.service.contracts;

import com.example.aiexpensetracker.core.model.Expense;

import java.time.LocalDate;
import java.util.List;

public interface IExpenseService {
    List<Expense> getAllExpensesByUser(String userEmail);

    Expense createExpense(Expense expense, String userEmail);

    Expense updateExpense(Long id, Expense expenseDetails);

    void deleteExpense(Long id);

    List<Expense> getExpensesByDateRange(String userEmail, LocalDate startDate, LocalDate endDate);
}
