package com.example.aiexpensetracker.core.service.expense;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManager;
import com.example.aiexpensetracker.exception.category.CategoryNotFoundException;
import com.example.aiexpensetracker.exception.expense.ExpenseNotFoundException;
import com.example.aiexpensetracker.exception.expense.ExpenseOwnershipException;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.expense.CreateExpenseDTO;
import com.example.aiexpensetracker.rest.dto.expense.ExpenseResponseDTO;
import com.example.aiexpensetracker.rest.dto.expense.UpdateExpenseDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final RepositoryManager repositoryManager;

    public ExpenseServiceImpl(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Async
    @Override
    public CompletableFuture<List<ExpenseResponseDTO>> getAllExpensesByUser(
            String userEmail,
            String sortField,
            String direction
    ) {
        return CompletableFuture.supplyAsync(() -> {
            List<Expense> expenses = repositoryManager.getExpenseRepository()
                    .findByUserEmail(userEmail);

            // Create final local variables
            final String finalSortField = (sortField == null || sortField.isEmpty())
                    ? "date"
                    : sortField;
            final String finalDirection = (direction == null || direction.isEmpty())
                    ? "asc"
                    : direction;

            if (finalSortField.equalsIgnoreCase("amount")) {
                expenses.sort(finalDirection.equalsIgnoreCase("desc")
                        ? Comparator.comparing(Expense::getAmount).reversed()
                        : Comparator.comparing(Expense::getAmount));
            } else {
                expenses.sort(finalDirection.equalsIgnoreCase("desc")
                        ? Comparator.comparing(Expense::getDate).reversed()
                        : Comparator.comparing(Expense::getDate));
            }

            return expenses.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        });
    }


    @Async
    @Override
    public CompletableFuture<ExpenseResponseDTO> createExpense(CreateExpenseDTO dto, String userEmail) {
        return CompletableFuture.supplyAsync(() -> {
            User user = repositoryManager.getUserRepository()
                    .findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

            Category category = null;
            if (dto.getCategoryId() != null) {
                category = repositoryManager.getCategoryRepository()
                        .findById(dto.getCategoryId())
                        .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + dto.getCategoryId()));
            }

            Expense expense = new Expense();
            expense.setUser(user);
            expense.setCategory(category);
            expense.setAmount(dto.getAmount());
            expense.setDate(dto.getDate());
            expense.setDescription(dto.getDescription());

            Expense saved = repositoryManager.getExpenseRepository().save(expense);
            return mapToResponseDTO(saved);
        });
    }

    @Async
    @Override
    public CompletableFuture<ExpenseResponseDTO> updateExpense(
            String userEmail,
            Long expenseId,
            UpdateExpenseDTO dto
    ) {
        return CompletableFuture.supplyAsync(() -> {
            User user = repositoryManager.getUserRepository()
                    .findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

            Expense expense = repositoryManager.getExpenseRepository()
                    .findById(expenseId)
                    .orElseThrow(() -> new ExpenseNotFoundException("Expense not found: " + expenseId));

            if (!expense.getUser().getId().equals(user.getId())) {
                throw new ExpenseOwnershipException("User does not own this expense.");
            }

            if (dto.getCategoryId() != null) {
                Category category = repositoryManager.getCategoryRepository()
                        .findById(dto.getCategoryId())
                        .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + dto.getCategoryId()));
                expense.setCategory(category);
            }

            expense.setAmount(dto.getAmount());
            expense.setDate(dto.getDate());
            expense.setDescription(dto.getDescription());

            Expense updated = repositoryManager.getExpenseRepository().save(expense);
            return mapToResponseDTO(updated);
        });
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteExpense(String userEmail, Long expenseId) {
        return CompletableFuture.runAsync(() -> {
            User user = repositoryManager.getUserRepository()
                    .findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

            Expense expense = repositoryManager.getExpenseRepository()
                    .findById(expenseId)
                    .orElseThrow(() -> new ExpenseNotFoundException("Expense not found: " + expenseId));

            if (!expense.getUser().getId().equals(user.getId())) {
                throw new ExpenseOwnershipException("User does not own this expense.");
            }

            repositoryManager.getExpenseRepository().delete(expense);
        });
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
}
