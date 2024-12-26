package com.example.aiexpensetracker.core.repository.manager;

import com.example.aiexpensetracker.core.repository.expense.IExpenseRepository;
import com.example.aiexpensetracker.core.repository.category.ICategoryRepository;
import com.example.aiexpensetracker.core.repository.user.IUserRepository;

public interface IRepositoryManager {
    IUserRepository getUserRepository();

    IExpenseRepository getExpenseRepository();

    ICategoryRepository getCategoryRepository();
}
