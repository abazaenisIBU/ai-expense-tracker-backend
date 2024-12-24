package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.RepositoryManager;
import com.example.aiexpensetracker.core.service.contracts.IExpenseService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService implements IExpenseService {
    private final RepositoryManager repositoryManager;

    public ExpenseService(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public List<Expense> getAllExpensesByUser(String userEmail) {
        return repositoryManager.getExpenseRepository().findByUserEmail(userEmail);
    }

    public Expense createExpense(Expense expense, String userEmail) {
        User user = repositoryManager.getUserRepository().findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        expense.setUser(user);
        return repositoryManager.getExpenseRepository().save(expense);
    }

    public Expense updateExpense(Long id, Expense expenseDetails) {
        Expense expense = repositoryManager.getExpenseRepository().findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setAmount(expenseDetails.getAmount());
        expense.setDate(expenseDetails.getDate());
        expense.setDescription(expenseDetails.getDescription());
        expense.setCategory(expenseDetails.getCategory());
        return repositoryManager.getExpenseRepository().save(expense);
    }

    public void deleteExpense(Long id) {
        repositoryManager.getExpenseRepository().deleteById(id);
    }

    public List<Expense> getExpensesByDateRange(String userEmail, LocalDate startDate, LocalDate endDate) {
        return repositoryManager.getExpenseRepository().findByUserEmailAndDateBetween(userEmail, startDate, endDate);
    }
}