package com.example.aiexpensetracker.unit.exception.exception;

import com.example.aiexpensetracker.exception.ErrorResponse;
import com.example.aiexpensetracker.exception.GlobalExceptionHandler;
import com.example.aiexpensetracker.exception.category.CategoryNotFoundException;
import com.example.aiexpensetracker.exception.expense.ExpenseNotFoundException;
import com.example.aiexpensetracker.exception.expense.ExpenseOwnershipException;
import com.example.aiexpensetracker.exception.user.EmailAlreadyInUseException;
import com.example.aiexpensetracker.exception.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("UserNotFound returns 404 with correct body")
    void handleUserNotFound() {
        UserNotFoundException ex = new UserNotFoundException("No user 7");

        ResponseEntity<ErrorResponse> response = handler.handleUserNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User Not Found", Objects.requireNonNull(response.getBody()).getError());
        assertEquals("No user 7", response.getBody().getDetails().getFirst().getMessage());
    }

    @Test
    @DisplayName("CategoryNotFound returns 404")
    void handleCategoryNotFound() {
        ResponseEntity<ErrorResponse> response =
                handler.handleCategoryNotFound(new CategoryNotFoundException("cat-id 9"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category Not Found", Objects.requireNonNull(response.getBody()).getError());
    }

    @Test
    @DisplayName("ExpenseNotFound returns 404")
    void handleExpenseNotFound() {
        ResponseEntity<ErrorResponse> response =
                handler.handleExpenseNotFound(new ExpenseNotFoundException("exp-id 3"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Expense Not Found", Objects.requireNonNull(response.getBody()).getError());
    }

    @Test
    @DisplayName("ExpenseOwnership returns 403")
    void handleOwnership() {
        ResponseEntity<ErrorResponse> response =
                handler.handleExpenseOwnershipException(new ExpenseOwnershipException("not owner"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Ownership Error", Objects.requireNonNull(response.getBody()).getError());
    }

    @Test
    @DisplayName("EmailAlreadyInUse returns 409 and exposes field=email")
    void handleEmailAlreadyInUse() {
        ResponseEntity<ErrorResponse> response =
                handler.handleEmailAlreadyInUse(new EmailAlreadyInUseException("test@mail.com"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorResponse.ErrorDetail d = response.getBody().getDetails().getFirst();
        assertEquals("email", d.getField());
        assertEquals("test@mail.com", d.getMessage());
    }

    @Test
    @DisplayName("Validation errors are mapped to BAD_REQUEST with field list")
    void handleMethodArgumentNotValid() {
        BindingResult br = mock(BindingResult.class);
        when(br.getFieldErrors()).thenReturn(
                List.of(new FieldError("dto", "amount", "must be >= 0"))
        );

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, br);

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorResponse.ErrorDetail detail = response.getBody().getDetails().getFirst();
        assertEquals("amount", detail.getField());
        assertEquals("must be >= 0", detail.getMessage());
    }

    @Test
    @DisplayName("DataIntegrityViolation with duplicate key returns 409 and parses detail")
    void handleDuplicateKey() {
        String pgMsg =
                "ERROR: duplicate key value violates unique constraint \"uk_email\"\n" +
                        "  Detail: Key (email)=(foo@bar.com) already exists.";
        RuntimeException rootCause = new RuntimeException(pgMsg);
        DataIntegrityViolationException ex = new DataIntegrityViolationException("dup", rootCause);

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getDetails().getFirst().getMessage()
                .contains("Key (email)=(foo@bar.com) already exists"));
    }

    @Test
    @DisplayName("Generic exception yields 500")
    void handleGeneric() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGenericExceptions(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getError());
    }
}