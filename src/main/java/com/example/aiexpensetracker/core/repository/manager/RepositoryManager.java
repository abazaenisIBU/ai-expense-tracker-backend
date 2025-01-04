package com.example.aiexpensetracker.core.repository.manager;

import com.example.aiexpensetracker.core.repository.expense.ExpenseRepository;
import com.example.aiexpensetracker.core.repository.category.CategoryRepository;
import com.example.aiexpensetracker.core.repository.user.UserRepository;

public interface RepositoryManager {
    UserRepository getUserRepository();

    ExpenseRepository getExpenseRepository();

    CategoryRepository getCategoryRepository();
}
