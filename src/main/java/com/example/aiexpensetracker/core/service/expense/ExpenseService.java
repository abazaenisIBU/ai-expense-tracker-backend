package com.example.aiexpensetracker.core.service.expense;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.manager.IRepositoryManager;
import com.example.aiexpensetracker.exception.category.CategoryNotFoundException;
import com.example.aiexpensetracker.exception.expense.ExpenseNotFoundException;
import com.example.aiexpensetracker.exception.expense.ExpenseOwnershipException;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.expense.*;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService implements IExpenseService {

    private final IRepositoryManager repositoryManager;

    public ExpenseService(IRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public List<ExpenseResponseDTO> getAllExpensesByUser(String userEmail, String sortField, String direction) {
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
                expenses.sort(direction.equalsIgnoreCase("desc")
                        ? Comparator.comparing(Expense::getAmount).reversed()
                        : Comparator.comparing(Expense::getAmount));
                break;
            default:
                expenses.sort(direction.equalsIgnoreCase("desc")
                        ? Comparator.comparing(Expense::getDate).reversed()
                        : Comparator.comparing(Expense::getDate));
        }

        return expenses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ExpenseResponseDTO createExpense(CreateExpenseDTO dto, String userEmail) {
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

        return mapToResponseDTO(repositoryManager.getExpenseRepository().save(expense));
    }

    public ExpenseResponseDTO updateExpense(String userEmail, Long expenseId, UpdateExpenseDTO dto) {
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

        return mapToResponseDTO(repositoryManager.getExpenseRepository().save(expense));
    }

    public void deleteExpense(String userEmail, Long expenseId) {
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