package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.expense.ExpenseRepository;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManager;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import com.example.aiexpensetracker.core.service.report.ReportServiceImpl;
import com.example.aiexpensetracker.rest.dto.report.CategoryTotalDTO;
import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;
import com.example.aiexpensetracker.rest.dto.statistics.ExpenseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportServiceImplTest {

    @Mock
    private RepositoryManager repositoryManager;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User testUser;
    private Expense testExpense;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(repositoryManager.getExpenseRepository()).thenReturn(expenseRepository);
        when(repositoryManager.getUserRepository()).thenReturn(userRepository);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        // Create test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Groceries");

        // Create test expense
        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setUser(testUser);
        testExpense.setCategory(testCategory);
        testExpense.setAmount(BigDecimal.valueOf(100.00));
        testExpense.setDate(LocalDate.now());
        testExpense.setDescription("Test expense");
    }

    // --- Test: generateReportsForAllUsers ---
    @Test
    void testGenerateReportsForAllUsers_Success() throws Exception {
        List<User> users = List.of(testUser);
        List<Expense> expenses = List.of(testExpense);

        when(userRepository.findAll()).thenReturn(users);
        when(expenseRepository.findByUserEmailAndDateBetween(
                eq(testUser.getEmail()), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expenses);

        CompletableFuture<List<UserReportResponseDTO>> resultFuture =
                reportService.generateReportsForAllUsers(LocalDate.now().minusDays(30), LocalDate.now());

        List<UserReportResponseDTO> result = resultFuture.get();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getUserEmail());
        assertEquals(BigDecimal.valueOf(100.00), result.get(0).getTotalAmount());
    }

    // --- Test: generateReportForUser ---
    @Test
    void testGenerateReportForUser_Success() {
        // Given
        List<Expense> expenses = List.of(testExpense);
        List<User> users = List.of(testUser);

        when(userRepository.findAll()).thenReturn(users);
        when(expenseRepository.findByUserEmailAndDateBetween(
                eq(testUser.getEmail()), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expenses);

        // When
        CompletableFuture<List<UserReportResponseDTO>> resultFuture = reportService
                .generateReportsForAllUsers(LocalDate.now().minusDays(30), LocalDate.now());

        List<UserReportResponseDTO> result = resultFuture.join();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        UserReportResponseDTO report = result.get(0);
        assertEquals(testUser.getEmail(), report.getUserEmail());
        assertEquals(BigDecimal.valueOf(100.00), report.getTotalAmount());
        assertEquals(1, report.getCategoryTotals().size());
    }

    // --- Test: calculateTotalAmount ---
    @Test
    void testCalculateTotalAmount() {
        List<Expense> expenses = List.of(testExpense);

        BigDecimal totalAmount = reportService.calculateTotalAmount(expenses);

        assertEquals(BigDecimal.valueOf(100.00), totalAmount);
    }

    // --- Test: calculateCategoryTotals ---
    @Test
    void testCalculateCategoryTotals() {
        List<Expense> expenses = List.of(testExpense);

        List<CategoryTotalDTO> categoryTotals = reportService.calculateCategoryTotals(expenses);

        assertNotNull(categoryTotals);
        assertEquals(1, categoryTotals.size());
        assertEquals("Groceries", categoryTotals.get(0).getCategoryName());
        assertEquals(BigDecimal.valueOf(100.00), categoryTotals.get(0).getTotalAmount());
    }

    // --- Test: mapToExpenseDTOs ---
    @Test
    void testMapToExpenseDTOs() {
        List<Expense> expenses = List.of(testExpense);

        List<ExpenseDTO> expenseDTOs = reportService.mapToExpenseDTOs(expenses);

        assertNotNull(expenseDTOs);
        assertEquals(1, expenseDTOs.size());
        assertEquals(testExpense.getId(), expenseDTOs.get(0).getId());
        assertEquals(testExpense.getAmount(), expenseDTOs.get(0).getAmount());
    }

    // --- Test: handleEmptyExpenses ---
    @Test
    void testGenerateReportsForAllUsers_NoExpenses() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(expenseRepository.findByUserEmailAndDateBetween(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        CompletableFuture<List<UserReportResponseDTO>> resultFuture =
                reportService.generateReportsForAllUsers(LocalDate.now().minusDays(30), LocalDate.now());

        List<UserReportResponseDTO> result = resultFuture.get();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.ZERO, result.get(0).getTotalAmount());
        assertTrue(result.get(0).getCategoryTotals().isEmpty());
    }
}
