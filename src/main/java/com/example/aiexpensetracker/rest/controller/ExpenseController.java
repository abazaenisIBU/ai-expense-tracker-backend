package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.rest.dto.expense.CreateExpenseDTO;
import com.example.aiexpensetracker.rest.dto.expense.ExpenseResponseDTO;
import com.example.aiexpensetracker.rest.dto.expense.UpdateExpenseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for managing expenses in the AI Expense Tracker application.
 * Provides endpoints for creating, retrieving, updating, and deleting expenses.
 */
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ServiceManager serviceManager;

    /**
     * Constructs a new ExpenseController with the provided ServiceManager.
     *
     * @param serviceManager the ServiceManager instance that provides access to various services.
     */
    public ExpenseController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Retrieves all expenses associated with a specific user.
     *
     * @param email     the email of the user whose expenses are to be retrieved.
     * @param sortBy    optional parameter to specify the field to sort by (e.g., "amount" or "date").
     * @param direction optional parameter to specify the sorting direction ("asc" or "desc").
     * @return a CompletableFuture containing a ResponseEntity with a list of ExpenseResponseDTO objects.
     */
    @GetMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<List<ExpenseResponseDTO>>> getAllExpensesByUser(
            @PathVariable String email,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        return serviceManager.getExpenseService()
                .getAllExpensesByUser(email, sortBy, direction)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Creates a new expense for a specific user.
     *
     * @param email             the email of the user for whom the expense is being created.
     * @param createExpenseDTO   the request body containing the expense details.
     * @return a CompletableFuture containing a ResponseEntity with the created ExpenseResponseDTO and HTTP status 201 (Created).
     */
    @PostMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<ExpenseResponseDTO>> createExpense(
            @PathVariable String email,
            @Valid @RequestBody CreateExpenseDTO createExpenseDTO
    ) {
        return serviceManager.getExpenseService()
                .createExpense(createExpenseDTO, email)
                .thenApply(created -> ResponseEntity.status(201).body(created));
    }

    /**
     * Updates an existing expense for a specific user.
     *
     * @param email             the email of the user who owns the expense.
     * @param id                the ID of the expense to be updated.
     * @param updateExpenseDTO   the request body containing the updated expense details.
     * @return a CompletableFuture containing a ResponseEntity with the updated ExpenseResponseDTO.
     */
    @PutMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<ExpenseResponseDTO>> updateExpense(
            @PathVariable String email,
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseDTO updateExpenseDTO
    ) {
        return serviceManager.getExpenseService()
                .updateExpense(email, id, updateExpenseDTO)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Deletes an expense for a specific user.
     *
     * @param email the email of the user who owns the expense.
     * @param id    the ID of the expense to be deleted.
     * @return a CompletableFuture containing a ResponseEntity with HTTP status 204 (No Content) if the deletion is successful.
     */
    @DeleteMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteExpense(
            @PathVariable String email,
            @PathVariable Long id
    ) {
        return serviceManager.getExpenseService()
                .deleteExpense(email, id)
                .thenApply(ignored -> ResponseEntity.noContent().build());
    }
}
