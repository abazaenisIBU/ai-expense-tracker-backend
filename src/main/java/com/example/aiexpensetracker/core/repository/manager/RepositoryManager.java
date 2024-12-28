package com.example.aiexpensetracker.core.repository.manager;

import com.example.aiexpensetracker.core.repository.expense.IExpenseRepository;
import com.example.aiexpensetracker.core.repository.user.IUserRepository;
import com.example.aiexpensetracker.core.repository.category.ICategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class RepositoryManager implements IRepositoryManager {
    private final IUserRepository IUserRepository;
    private final IExpenseRepository IExpenseRepository;
    private final ICategoryRepository ICategoryRepository;

    public RepositoryManager(IUserRepository IUserRepository, IExpenseRepository IExpenseRepository, ICategoryRepository ICategoryRepository) {
        this.IUserRepository = IUserRepository;
        this.IExpenseRepository = IExpenseRepository;
        this.ICategoryRepository = ICategoryRepository;
    }

    public IUserRepository getUserRepository() {
        return IUserRepository;
    }

    public IExpenseRepository getExpenseRepository() {
        return IExpenseRepository;
    }

    public ICategoryRepository getCategoryRepository() {
        return ICategoryRepository;
    }
}
