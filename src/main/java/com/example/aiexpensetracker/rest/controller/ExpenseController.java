package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.IServiceManager;
import com.example.aiexpensetracker.rest.dto.expense.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final IServiceManager serviceManager;

    public ExpenseController(IServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpensesByUser(
            @PathVariable String email,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        List<ExpenseResponseDTO> expenses = serviceManager.getExpenseService()
                .getAllExpensesByUser(email, sortBy, direction);
        return ResponseEntity.ok(expenses);
    }

    @PostMapping("/user/{email}")
    public ResponseEntity<ExpenseResponseDTO> createExpense(
            @PathVariable String email,
            @Valid @RequestBody CreateExpenseDTO createExpenseDTO
    ) {
        ExpenseResponseDTO createdExpense = serviceManager.getExpenseService()
                .createExpense(createExpenseDTO, email);
        return ResponseEntity.status(201).body(createdExpense);
    }

    @PutMapping("/user/{email}/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @PathVariable String email,
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseDTO updateExpenseDTO
    ) {
        ExpenseResponseDTO updatedExpense = serviceManager.getExpenseService()
                .updateExpense(email, id, updateExpenseDTO);
        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/user/{email}/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable String email,
            @PathVariable Long id
    ) {
        serviceManager.getExpenseService().deleteExpense(email, id);
        return ResponseEntity.noContent().build();
    }
}