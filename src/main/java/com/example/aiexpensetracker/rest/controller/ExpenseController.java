package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.ServiceManager;
import com.example.aiexpensetracker.rest.dto.expense.*;
import com.example.aiexpensetracker.rest.dto.shared.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public CompletableFuture<ResponseEntity<ApiResponse<List<ExpenseResponseDTO>>>> getAllExpensesByUser(
            @PathVariable String email,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ExpenseResponseDTO> list = serviceManager.getExpenseService()
                        .getAllExpensesByUser(email, sortBy, direction);

                ApiResponse<List<ExpenseResponseDTO>> body = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Expenses fetched successfully",
                        list
                );
                return ResponseEntity.ok(body);

            } catch (RuntimeException e) {
                ApiResponse<List<ExpenseResponseDTO>> error = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        });
    }

    @PostMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<ApiResponse<ExpenseResponseDTO>>> createExpense(
            @PathVariable String email,
            @Valid @RequestBody CreateExpenseDTO createExpenseDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExpenseResponseDTO created = serviceManager.getExpenseService()
                        .createExpense(createExpenseDTO, email);

                ApiResponse<ExpenseResponseDTO> success = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.CREATED.value(),
                        "Expense created successfully",
                        created
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(success);

            } catch (RuntimeException e) {
                // "User not found" or "Category not found"
                ApiResponse<ExpenseResponseDTO> notFound = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);

            }
        });
    }

    @PutMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<ExpenseResponseDTO>>> updateExpense(
            @PathVariable String email,
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseDTO updateExpenseDTO
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExpenseResponseDTO updated = serviceManager.getExpenseService()
                        .updateExpense(email, id, updateExpenseDTO);

                ApiResponse<ExpenseResponseDTO> body = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Expense updated successfully",
                        updated
                );
                return ResponseEntity.ok(body);

            } catch (RuntimeException e) {
                // "User not found" or "Expense not found" or ownership mismatch
                ApiResponse<ExpenseResponseDTO> notFound = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);

            }
        });
    }

    @DeleteMapping("/user/{email}/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> deleteExpense(
            @PathVariable String email,
            @PathVariable Long id
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                serviceManager.getExpenseService().deleteExpense(email, id);

                ApiResponse<Void> success = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.NO_CONTENT.value(),
                        "Expense deleted successfully",
                        null
                );
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(success);

            } catch (RuntimeException e) {
                // "User not found" or "Expense not found" or ownership mismatch
                ApiResponse<Void> notFound = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        });
    }

    @GetMapping("/user/{email}/date-range")
    public CompletableFuture<ResponseEntity<ApiResponse<List<ExpenseResponseDTO>>>> getExpensesByDateRange(
            @PathVariable String email,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ExpenseResponseDTO> list = serviceManager.getExpenseService()
                        .getExpensesByDateRange(email, startDate, endDate);

                ApiResponse<List<ExpenseResponseDTO>> body = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Expenses fetched by date range",
                        list
                );
                return ResponseEntity.ok(body);

            } catch (RuntimeException e) {
                ApiResponse<List<ExpenseResponseDTO>> notFound = new ApiResponse<>(
                        java.time.LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        });
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        StringBuilder sb = new StringBuilder("Validation error(s): ");

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            sb.append("'")
                    .append(error.getField())
                    .append("' ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });

        ApiResponse<Void> response = new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                sb.toString(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }
}
