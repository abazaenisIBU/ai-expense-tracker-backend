package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.repository.expense.ExpenseRepository;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManager;
import com.example.aiexpensetracker.core.service.statistics.StatisticsServiceImpl;
import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatisticsServiceImplTest {

    @Mock
    private RepositoryManager repositoryManager;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private Expense testExpense;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(repositoryManager.getExpenseRepository()).thenReturn(expenseRepository);

        // Create test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Groceries");

        // Create test expense
        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setCategory(testCategory);
        testExpense.setAmount(BigDecimal.valueOf(100.00));
        testExpense.setDate(LocalDate.now());
        testExpense.setDescription("Test expense");
    }

    // --- Test: getStatisticsForUser ---
    @Test
    void testGetStatisticsForUser_Success() throws Exception {
        // Given
        String email = "test@example.com";
        List<Expense> expenses = List.of(testExpense);

        when(expenseRepository.findByUserEmail(email)).thenReturn(expenses);

        // When
        CompletableFuture<StatisticsResponseDTO> resultFuture = statisticsService.getStatisticsForUser(email);
        StatisticsResponseDTO result = resultFuture.get();

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCategoryStatistics().size());
        assertEquals("Groceries", result.getCategoryStatistics().get(0).getCategoryName());
        assertEquals(BigDecimal.valueOf(100.00), result.getCategoryStatistics().get(0).getTotalAmount());
        assertEquals(1, result.getMonthlyStatistics().size());
        assertEquals(BigDecimal.valueOf(100.00), result.getMonthlyStatistics().get(0).getTotalAmount());
    }

    @Test
    void testGetStatisticsForUser_NoExpenses() throws Exception {
        // Given
        String email = "test@example.com";

        when(expenseRepository.findByUserEmail(email)).thenReturn(List.of());

        // When
        CompletableFuture<StatisticsResponseDTO> resultFuture = statisticsService.getStatisticsForUser(email);
        StatisticsResponseDTO result = resultFuture.get();

        // Then
        assertNotNull(result);
        assertTrue(result.getCategoryStatistics().isEmpty());
        assertTrue(result.getMonthlyStatistics().isEmpty());
    }

    // --- Test: calculateStatisticsByCategory ---
    @Test
    void testCalculateStatisticsByCategory() throws Exception {
        // Given
        String email = "test@example.com";
        List<Expense> expenses = List.of(testExpense);

        when(expenseRepository.findByUserEmail(email)).thenReturn(expenses);

        // When
        CompletableFuture<StatisticsResponseDTO> resultFuture = statisticsService.getStatisticsForUser(email);
        StatisticsResponseDTO result = resultFuture.get();

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCategoryStatistics().size());
        assertEquals("Groceries", result.getCategoryStatistics().get(0).getCategoryName());
    }

    // --- Test: calculateStatisticsByMonth ---
    @Test
    void testCalculateStatisticsByMonth() throws Exception {
        // Given
        String email = "test@example.com";
        List<Expense> expenses = List.of(testExpense);

        when(expenseRepository.findByUserEmail(email)).thenReturn(expenses);

        // When
        CompletableFuture<StatisticsResponseDTO> resultFuture = statisticsService.getStatisticsForUser(email);
        StatisticsResponseDTO result = resultFuture.get();

        // Then
        assertNotNull(result);
        assertEquals(1, result.getMonthlyStatistics().size());
        assertEquals(testExpense.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                result.getMonthlyStatistics().get(0).getMonthYear());
    }
}
