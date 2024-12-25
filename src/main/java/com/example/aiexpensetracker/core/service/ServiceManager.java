package com.example.aiexpensetracker.core.service;

import com.example.aiexpensetracker.core.service.contracts.IServiceManager;
import org.springframework.stereotype.Component;

@Component
public class ServiceManager implements IServiceManager {
    private final UserService userService;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final ReportService reportService;

    public ServiceManager(UserService userService, ExpenseService expenseService, CategoryService categoryService, ReportService reportService) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.reportService = reportService;
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

    public ReportService getReportService() {
        return reportService;
    }
}
