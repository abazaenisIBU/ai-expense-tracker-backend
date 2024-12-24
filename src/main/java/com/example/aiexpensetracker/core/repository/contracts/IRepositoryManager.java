package com.example.aiexpensetracker.core.repository.contracts;

public interface IRepositoryManager {
    UserRepository getUserRepository();

    ExpenseRepository getExpenseRepository();

    CategoryRepository getCategoryRepository();
}
