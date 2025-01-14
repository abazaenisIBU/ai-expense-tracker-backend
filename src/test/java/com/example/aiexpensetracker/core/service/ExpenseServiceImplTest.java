package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.Expense;
import com.example.aiexpensetracker.core.model.User;
import com.example.aiexpensetracker.core.repository.expense.ExpenseRepository;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManager;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import com.example.aiexpensetracker.core.service.expense.ExpenseServiceImpl;
import com.example.aiexpensetracker.exception.expense.ExpenseNotFoundException;
import com.example.aiexpensetracker.exception.expense.ExpenseOwnershipException;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import com.example.aiexpensetracker.rest.dto.expense.CreateExpenseDTO;
import com.example.aiexpensetracker.rest.dto.expense.ExpenseResponseDTO;
import com.example.aiexpensetracker.rest.dto.expense.UpdateExpenseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseServiceImplTest {

    @Mock
    private RepositoryManager repositoryManager;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

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

    // --- Test: getAllExpensesByUser ---
    @Test
    void testGetAllExpensesByUser_Success() throws Exception {
        List<Expense> expenses = new ArrayList<>(List.of(testExpense));

        when(expenseRepository.findByUserEmail(testUser.getEmail()))
                .thenReturn(expenses);

        CompletableFuture<List<ExpenseResponseDTO>> resultFuture =
                expenseService.getAllExpensesByUser(testUser.getEmail(), "date", "asc", null, null);

        List<ExpenseResponseDTO> result = resultFuture.get();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testExpense.getAmount(), result.get(0).getAmount());
    }

    // --- Test: createExpense ---
    @Test
    void testCreateExpense_Success() throws Exception {
        CreateExpenseDTO dto = new CreateExpenseDTO();
        dto.setAmount(BigDecimal.valueOf(200.00));
        dto.setDate(LocalDate.now());
        dto.setDescription("New expense");

        when(repositoryManager.getUserRepository().findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        CompletableFuture<ExpenseResponseDTO> resultFuture = expenseService.createExpense(dto, testUser.getEmail());
        ExpenseResponseDTO result = resultFuture.get();

        assertNotNull(result);
        assertEquals(testExpense.getAmount(), result.getAmount());
    }

    @Test
    void testCreateExpense_UserNotFound() {
        CreateExpenseDTO dto = new CreateExpenseDTO();
        dto.setAmount(BigDecimal.valueOf(200.00));

        when(repositoryManager.getUserRepository().findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        CompletableFuture<ExpenseResponseDTO> resultFuture =
                expenseService.createExpense(dto, "nonexistent@example.com");

        assertThrows(UserNotFoundException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });

        verify(repositoryManager.getUserRepository(), times(1)).findByEmail("nonexistent@example.com");
    }

    // --- Test: updateExpense ---
    @Test
    void testUpdateExpense_Success() throws Exception {
        UpdateExpenseDTO dto = new UpdateExpenseDTO();
        dto.setAmount(BigDecimal.valueOf(150.00));
        dto.setDate(LocalDate.now());

        when(repositoryManager.getUserRepository().findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(expenseRepository.findById(testExpense.getId())).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        CompletableFuture<ExpenseResponseDTO> resultFuture =
                expenseService.updateExpense(testUser.getEmail(), testExpense.getId(), dto);
        ExpenseResponseDTO result = resultFuture.get();

        assertNotNull(result);
        assertEquals(testExpense.getAmount(), result.getAmount());
    }

    @Test
    void testUpdateExpense_ExpenseNotFound() {
        UpdateExpenseDTO dto = new UpdateExpenseDTO();
        dto.setAmount(BigDecimal.valueOf(150.00));

        when(repositoryManager.getUserRepository().findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        CompletableFuture<ExpenseResponseDTO> resultFuture =
                expenseService.updateExpense(testUser.getEmail(), 99L, dto);

        assertThrows(ExpenseNotFoundException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }

    // --- Test: deleteExpense ---
    @Test
    void testDeleteExpense_Success() {
        when(repositoryManager.getUserRepository().findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(expenseRepository.findById(testExpense.getId())).thenReturn(Optional.of(testExpense));

        CompletableFuture<Void> resultFuture =
                expenseService.deleteExpense(testUser.getEmail(), testExpense.getId());

        assertDoesNotThrow(resultFuture::join);
        verify(expenseRepository, times(1)).delete(testExpense);
    }

    @Test
    void testDeleteExpense_ExpenseNotFound() {
        when(repositoryManager.getUserRepository().findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        CompletableFuture<Void> resultFuture =
                expenseService.deleteExpense(testUser.getEmail(), 99L);

        assertThrows(ExpenseNotFoundException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }

    @Test
    void testDeleteExpense_ExpenseOwnershipException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");

        testExpense.setUser(anotherUser);

        when(repositoryManager.getUserRepository().findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(expenseRepository.findById(testExpense.getId()))
                .thenReturn(Optional.of(testExpense));

        CompletableFuture<Void> resultFuture =
                expenseService.deleteExpense(testUser.getEmail(), testExpense.getId());

        assertThrows(ExpenseOwnershipException.class, () -> {
            try {
                resultFuture.join();
            } catch (CompletionException ex) {
                throw (RuntimeException) ex.getCause();
            }
        });
    }
}
