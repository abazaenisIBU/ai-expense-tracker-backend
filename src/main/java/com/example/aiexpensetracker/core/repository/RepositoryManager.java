package com.example.aiexpensetracker.core.repository;

import com.example.aiexpensetracker.core.repository.contracts.CategoryRepository;
import com.example.aiexpensetracker.core.repository.contracts.ExpenseRepository;
import com.example.aiexpensetracker.core.repository.contracts.IRepositoryManager;
import com.example.aiexpensetracker.core.repository.contracts.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class RepositoryManager implements IRepositoryManager {
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public RepositoryManager(UserRepository userRepository, ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ExpenseRepository getExpenseRepository() {
        return expenseRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }
}
