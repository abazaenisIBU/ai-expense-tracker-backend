package com.example.aiexpensetracker.core.service.expense;

import com.example.aiexpensetracker.rest.dto.expense.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for handling operations related to expenses in the AI Expense Tracker application.
 * Provides asynchronous methods for expense retrieval, creation, update, and deletion for a specific user.
 */
public interface ExpenseService {

    /**
     * Retrieves a list of all expenses associated with a specific user, with optional sorting.
     *
     * @param userEmail the email of the user whose expenses are to be retrieved.
     * @param sortBy    the field to sort the results by (e.g., "amount" or "date"). Defaults to "date" if null or empty.
     * @param direction the sorting direction, either "asc" for ascending or "desc" for descending. Defaults to "asc".
     * @return a CompletableFuture containing a list of ExpenseResponseDTO objects representing the user's expenses.
     */
    CompletableFuture<List<ExpenseResponseDTO>> getAllExpensesByUser(String userEmail, String sortBy, String direction);

    /**
     * Creates a new expense entry for a user.
     *
     * @param dto       the CreateExpenseDTO containing the expense details such as amount, date, and description.
     * @param userEmail the email of the user for whom the expense is being created.
     * @return a CompletableFuture containing an ExpenseResponseDTO representing the created expense.
     */
    CompletableFuture<ExpenseResponseDTO> createExpense(CreateExpenseDTO dto, String userEmail);

    /**
     * Updates an existing expense entry for a user.
     *
     * @param userEmail the email of the user who owns the expense.
     * @param expenseId the ID of the expense to be updated.
     * @param dto       the UpdateExpenseDTO containing the updated expense details.
     * @return a CompletableFuture containing an ExpenseResponseDTO representing the updated expense.
     */
    CompletableFuture<ExpenseResponseDTO> updateExpense(String userEmail, Long expenseId, UpdateExpenseDTO dto);

    /**
     * Deletes an existing expense entry for a user.
     *
     * @param userEmail the email of the user who owns the expense.
     * @param expenseId the ID of the expense to be deleted.
     * @return a CompletableFuture representing the completion of the delete operation.
     */
    CompletableFuture<Void> deleteExpense(String userEmail, Long expenseId);
}
