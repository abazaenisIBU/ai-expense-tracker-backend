package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.core.service.expense.ExpenseService;
import com.example.aiexpensetracker.rest.dto.expense.CreateExpenseDTO;
import com.example.aiexpensetracker.rest.dto.expense.ExpenseResponseDTO;
import com.example.aiexpensetracker.rest.dto.expense.UpdateExpenseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExpenseControllerTest {

    @Mock
    private ServiceManager serviceManager;

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(serviceManager.getExpenseService()).thenReturn(expenseService);
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testGetAllExpensesByUser() throws Exception {
        String email = "john.doe@example.com";
        ExpenseResponseDTO expense1 = new ExpenseResponseDTO();
        expense1.setId(1L);
        expense1.setDescription("Grocery shopping");
        expense1.setAmount(BigDecimal.valueOf(50.0));
        expense1.setDate(LocalDate.from(LocalDateTime.now().minusDays(1)));

        ExpenseResponseDTO expense2 = new ExpenseResponseDTO();
        expense2.setId(2L);
        expense2.setDescription("Utility bill");
        expense2.setAmount(BigDecimal.valueOf(100.0));
        expense2.setDate(LocalDate.from(LocalDateTime.now().minusDays(2)));

        List<ExpenseResponseDTO> mockExpenses = Arrays.asList(expense1, expense2);
        when(expenseService.getAllExpensesByUser(email, null, null, null, null))
                .thenReturn(CompletableFuture.completedFuture(mockExpenses));

        mockMvc.perform(get("/api/expenses/user/{email}", email))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testCreateExpense() throws Exception {
        String email = "john.doe@example.com";

        CreateExpenseDTO createExpenseDTO = new CreateExpenseDTO();
        createExpenseDTO.setDescription("Grocery shopping");
        createExpenseDTO.setAmount(BigDecimal.valueOf(50.0));
        createExpenseDTO.setDate(LocalDate.from(LocalDateTime.now()));  // Added date field to fix validation error

        ExpenseResponseDTO createdExpense = new ExpenseResponseDTO();
        createdExpense.setId(1L);
        createdExpense.setDescription("Grocery shopping");
        createdExpense.setAmount(BigDecimal.valueOf(50.0));
        createdExpense.setDate(LocalDate.from(LocalDateTime.now()));

        when(expenseService.createExpense(any(CreateExpenseDTO.class), eq(email)))
                .thenReturn(CompletableFuture.completedFuture(createdExpense));

        mockMvc.perform(post("/api/expenses/user/{email}", email)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createExpenseDTO)))
                .andReturn();

        verify(expenseService).createExpense(any(CreateExpenseDTO.class), eq(email));
    }

    @Test
    void testUpdateExpense() throws Exception {
        String email = "john.doe@example.com";
        Long id = 1L;

        UpdateExpenseDTO updateExpenseDTO = new UpdateExpenseDTO();
        updateExpenseDTO.setDescription("Updated Grocery shopping");
        updateExpenseDTO.setAmount(BigDecimal.valueOf(60.0));
        updateExpenseDTO.setDate(LocalDate.from(LocalDateTime.now()));  // Added date field to fix validation error

        ExpenseResponseDTO updatedExpense = new ExpenseResponseDTO();
        updatedExpense.setId(1L);
        updatedExpense.setDescription("Updated Grocery shopping");
        updatedExpense.setAmount(BigDecimal.valueOf(60.0));
        updatedExpense.setDate(LocalDate.from(LocalDateTime.now()));

        when(expenseService.updateExpense(anyString(), anyLong(), any(UpdateExpenseDTO.class)))
                .thenReturn(CompletableFuture.completedFuture(updatedExpense));

        mockMvc.perform(put("/api/expenses/user/{email}/{id}", email, id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateExpenseDTO)))
                .andExpect(status().isOk())
                .andReturn();

        verify(expenseService).updateExpense(eq(email), eq(id), any(UpdateExpenseDTO.class));
    }

    @Test
    void testDeleteExpense() throws Exception {
        String email = "john.doe@example.com";
        Long id = 1L;

        when(expenseService.deleteExpense(email, id))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(delete("/api/expenses/user/{email}/{id}", email, id));
    }
}
