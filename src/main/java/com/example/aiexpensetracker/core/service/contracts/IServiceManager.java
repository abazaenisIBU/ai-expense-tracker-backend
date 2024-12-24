package com.example.aiexpensetracker.core.service.contracts;

import com.example.aiexpensetracker.core.service.CategoryService;
import com.example.aiexpensetracker.core.service.ExpenseService;
import com.example.aiexpensetracker.core.service.UserService;

public interface IServiceManager {
    UserService getUserService();

    ExpenseService getExpenseService();

    CategoryService getCategoryService();
}
