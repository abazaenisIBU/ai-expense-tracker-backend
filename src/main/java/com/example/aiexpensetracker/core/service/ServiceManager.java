package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.service.contracts.IServiceManager;
import org.springframework.stereotype.Component;

@Component
public class ServiceManager implements IServiceManager {
    private final UserService userService;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;

    public ServiceManager(UserService userService, ExpenseService expenseService, CategoryService categoryService) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.categoryService = categoryService;
    }

    public UserService getUserService() {
        return userService;
    }

    public ExpenseService getExpenseService() {
        return expenseService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }
}
