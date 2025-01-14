package com.example.aiexpensetracker.core.repository.expense;

import com.example.aiexpensetracker.core.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for performing CRUD operations on the Expense entity.
 * Extends JpaRepository to provide basic database interaction methods.
 */
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Retrieves a list of expenses associated with a specific user's email.
     *
     * @param userEmail the email of the user whose expenses are to be retrieved.
     * @return a list of Expense entities associated with the given user's email.
     */
    List<Expense> findByUserEmail(String userEmail);

    /**
     * Retrieves a list of expenses for a specific user within a date range.
     *
     * @param userEmail the email of the user whose expenses are to be retrieved.
     * @param startDate the start date of the range.
     * @param endDate   the end date of the range.
     * @return a list of Expense entities that match the given user's email and fall within the specified date range.
     */
    List<Expense> findByUserEmailAndDateBetween(String userEmail, LocalDate startDate, LocalDate endDate);
}
