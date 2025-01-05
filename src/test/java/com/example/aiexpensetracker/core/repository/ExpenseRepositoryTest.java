package com.example.aiexpensetracker.core.repository;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.expense.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ExpenseRepositoryTest {

    @Mock
    private ExpenseRepository expenseRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
    }

    @Test
    void testFindByUserEmail_ExpensesExist() {
        // Given
        String userEmail = "test@example.com";

        Expense expense1 = new Expense();
        expense1.setId(1L);
        expense1.setUser(testUser);
        expense1.setAmount(BigDecimal.valueOf(50.00));
        expense1.setDate(LocalDate.of(2025, 1, 1));
        expense1.setDescription("Groceries");

        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setUser(testUser);
        expense2.setAmount(BigDecimal.valueOf(100.00));
        expense2.setDate(LocalDate.of(2025, 1, 2));
        expense2.setDescription("Utilities");

        List<Expense> mockExpenses = Arrays.asList(expense1, expense2);

        // Mock behavior
        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(mockExpenses);

        // When
        List<Expense> foundExpenses = expenseRepository.findByUserEmail(userEmail);

        // Then
        assertEquals(2, foundExpenses.size(), "Should return 2 expenses");
        assertEquals("Groceries", foundExpenses.get(0).getDescription(), "First expense description should match");
        assertEquals("Utilities", foundExpenses.get(1).getDescription(), "Second expense description should match");
    }

    @Test
    void testFindByUserEmail_NoExpensesExist() {
        // Given
        String userEmail = "test@example.com";

        // Mock behavior
        when(expenseRepository.findByUserEmail(userEmail)).thenReturn(Collections.emptyList());

        // When
        List<Expense> foundExpenses = expenseRepository.findByUserEmail(userEmail);

        // Then
        assertTrue(foundExpenses.isEmpty(), "No expenses should be found");
    }

    @Test
    void testFindByUserEmailAndDateBetween_ExpensesExist() {
        // Given
        String userEmail = "test@example.com";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUser(testUser);
        expense.setAmount(BigDecimal.valueOf(75.00));
        expense.setDate(LocalDate.of(2025, 1, 15));
        expense.setDescription("Dinner");

        List<Expense> mockExpenses = Collections.singletonList(expense);

        // Mock behavior
        when(expenseRepository.findByUserEmailAndDateBetween(userEmail, startDate, endDate)).thenReturn(mockExpenses);

        // When
        List<Expense> foundExpenses = expenseRepository.findByUserEmailAndDateBetween(userEmail, startDate, endDate);

        // Then
        assertEquals(1, foundExpenses.size(), "Should return 1 expense");
        assertEquals("Dinner", foundExpenses.get(0).getDescription(), "Expense description should match");
        assertEquals(LocalDate.of(2025, 1, 15), foundExpenses.get(0).getDate(), "Expense date should match");
    }

    @Test
    void testFindByUserEmailAndDateBetween_NoExpensesExist() {
        // Given
        String userEmail = "test@example.com";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        // Mock behavior
        when(expenseRepository.findByUserEmailAndDateBetween(userEmail, startDate, endDate)).thenReturn(Collections.emptyList());

        // When
        List<Expense> foundExpenses = expenseRepository.findByUserEmailAndDateBetween(userEmail, startDate, endDate);

        // Then
        assertTrue(foundExpenses.isEmpty(), "No expenses should be found");
    }
}
