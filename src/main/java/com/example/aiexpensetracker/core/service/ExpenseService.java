package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.RepositoryManager;
import com.example.aiexpensetracker.core.service.contracts.IExpenseService;
import com.example.aiexpensetracker.rest.dto.expense.CreateExpenseDTO;
import com.example.aiexpensetracker.rest.dto.expense.ExpenseResponseDTO;
import com.example.aiexpensetracker.rest.dto.expense.UpdateExpenseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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

        // 2) Handle default values if needed
        if (sortField == null || sortField.isEmpty()) {
            sortField = "date";  // fallback sort field
        }
        if (direction == null || direction.isEmpty()) {
            direction = "asc";   // fallback direction
        }

        // 3) Sort in memory using a Comparator
        switch (sortField.toLowerCase()) {
            case "amount":
                if (direction.equalsIgnoreCase("desc")) {
                    expenses.sort(Comparator.comparing(Expense::getAmount).reversed());
                } else {
                    expenses.sort(Comparator.comparing(Expense::getAmount));
                }
                break;

            case "date":
            default: // or handle other fields
                if (direction.equalsIgnoreCase("desc")) {
                    expenses.sort(Comparator.comparing(Expense::getDate).reversed());
                } else {
                    expenses.sort(Comparator.comparing(Expense::getDate));
                }
                break;
        }

        // 4) Map each Expense to a DTO
        return expenses.stream()
                .map(this::mapToResponseDTO)   // convert Expense -> ExpenseResponseDTO
                .collect(Collectors.toList());
    }


    // 2) Create expense
    public ExpenseResponseDTO createExpense(CreateExpenseDTO dto, String userEmail) {
        // find user
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expense.setDescription(dto.getDescription());

        // if category specified
        if (dto.getCategoryId() != null) {
            Category cat = repositoryManager.getCategoryRepository()
                    .findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            expense.setCategory(cat);
        }

        Expense saved = repositoryManager.getExpenseRepository().save(expense);
        return mapToResponseDTO(saved);
    }

    // 3) Update expense
    public ExpenseResponseDTO updateExpense(String userEmail, Long expenseId, UpdateExpenseDTO dto) {
        // find user
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // find expense
        Expense expense = repositoryManager.getExpenseRepository()
                .findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // check ownership
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Expense does not belong to this user");
        }

        // update fields
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

    // 4) Delete expense
    public void deleteExpense(String userEmail, Long expenseId) {
        // find user
        User user = repositoryManager.getUserRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // find expense
        Expense expense = repositoryManager.getExpenseRepository()
                .findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // check ownership
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Expense does not belong to this user");
        }

        repositoryManager.getExpenseRepository().delete(expense);
    }

    // 5) Date range
    public List<ExpenseResponseDTO> getExpensesByDateRange(String userEmail, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = repositoryManager.getExpenseRepository()
                .findByUserEmailAndDateBetween(userEmail, startDate, endDate);

        return expenses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper: map Expense → ExpenseResponseDTO
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