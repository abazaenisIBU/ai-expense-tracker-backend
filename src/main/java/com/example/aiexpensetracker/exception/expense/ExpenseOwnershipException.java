package com.example.aiexpensetracker.exception.expense;

public class ExpenseOwnershipException extends RuntimeException {
    public ExpenseOwnershipException(String message) {
        super(message);
    }
}
