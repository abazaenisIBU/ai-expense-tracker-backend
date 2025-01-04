package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.rest.dto.expense.CreateExpenseDTO;
import com.example.aiexpensetracker.rest.dto.expense.ExpenseResponseDTO;
import com.example.aiexpensetracker.rest.dto.expense.UpdateExpenseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ServiceManager serviceManager;

    public ExpenseController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @GetMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<List<ExpenseResponseDTO>>> getAllExpensesByUser(
            @PathVariable String email,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        return serviceManager.getExpenseService()
                .getAllExpensesByUser(email, sortBy, direction)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<ExpenseResponseDTO>> createExpense(
            @PathVariable String email,
            @Valid @RequestBody CreateExpenseDTO createExpenseDTO
    ) {
        return serviceManager.getExpenseService()
                .createExpense(createExpenseDTO, email)
                .thenApply(created -> ResponseEntity.status(201).body(created));
    }

    @PutMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<ExpenseResponseDTO>> updateExpense(
            @PathVariable String email,
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseDTO updateExpenseDTO
    ) {
        return serviceManager.getExpenseService()
                .updateExpense(email, id, updateExpenseDTO)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteExpense(
            @PathVariable String email,
            @PathVariable Long id
    ) {
        return serviceManager.getExpenseService()
                .deleteExpense(email, id)
                .thenApply(ignored -> ResponseEntity.noContent().build());
    }
}
