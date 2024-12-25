package com.example.aiexpensetracker.core.repository.contracts;

import com.example.aiexpensetracker.core.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Retrieve all expenses by user email
    List<Expense> findByUserEmail(String userEmail);

    // Retrieve all expenses by user email and date range
    List<Expense> findByUserEmailAndDateBetween(String userEmail, LocalDate startDate, LocalDate endDate);
}
