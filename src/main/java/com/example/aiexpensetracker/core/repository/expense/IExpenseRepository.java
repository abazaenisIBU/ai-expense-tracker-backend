package com.example.aiexpensetracker.core.repository.expense;

import com.example.aiexpensetracker.core.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.util.List;

public interface IExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserEmail(String userEmail);

    List<Expense> findByUserEmailAndDateBetween(String userEmail, LocalDate startDate, LocalDate endDate);
}
