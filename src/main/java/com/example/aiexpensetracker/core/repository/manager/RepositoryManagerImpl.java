package com.example.aiexpensetracker.core.repository.manager;

import com.example.aiexpensetracker.core.repository.expense.ExpenseRepository;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import com.example.aiexpensetracker.core.repository.category.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class RepositoryManagerImpl implements RepositoryManager {
    private final UserRepository UserRepository;
    private final ExpenseRepository ExpenseRepository;
    private final CategoryRepository CategoryRepository;

    public RepositoryManagerImpl(UserRepository UserRepository, ExpenseRepository ExpenseRepository, CategoryRepository CategoryRepository) {
        this.UserRepository = UserRepository;
        this.ExpenseRepository = ExpenseRepository;
        this.CategoryRepository = CategoryRepository;
    }

    public UserRepository getUserRepository() {
        return UserRepository;
    }

    public ExpenseRepository getExpenseRepository() {
        return ExpenseRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return CategoryRepository;
    }
}
