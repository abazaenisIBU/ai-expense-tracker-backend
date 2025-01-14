package com.example.aiexpensetracker.core.repository.manager;

import com.example.aiexpensetracker.core.repository.expense.ExpenseRepository;
import com.example.aiexpensetracker.core.repository.category.CategoryRepository;
import com.example.aiexpensetracker.core.repository.user.UserRepository;

/**
 * Interface for managing access to different repository layers in the AI Expense Tracker application.
 * Provides methods to retrieve the UserRepository, ExpenseRepository, and CategoryRepository instances.
 */
public interface RepositoryManager {

    /**
     * Retrieves the UserRepository instance for performing CRUD operations on User entities.
     *
     * @return an instance of UserRepository.
     */
    UserRepository getUserRepository();

    /**
     * Retrieves the ExpenseRepository instance for performing CRUD operations on Expense entities.
     *
     * @return an instance of ExpenseRepository.
     */
    ExpenseRepository getExpenseRepository();

    /**
     * Retrieves the CategoryRepository instance for performing CRUD operations on Category entities.
     *
     * @return an instance of CategoryRepository.
     */
    CategoryRepository getCategoryRepository();
}
