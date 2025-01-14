package com.example.aiexpensetracker.core.service.manager;

import com.example.aiexpensetracker.core.service.category.CategoryService;
import com.example.aiexpensetracker.core.service.expense.ExpenseService;
import com.example.aiexpensetracker.core.service.report.ReportService;
import com.example.aiexpensetracker.core.service.statistics.StatisticsService;
import com.example.aiexpensetracker.core.service.user.UserService;

/**
 * Interface for managing the core services of the AI Expense Tracker application.
 * Provides access to various service interfaces such as user management, expense tracking, category management, report generation, and statistics calculation.
 */
public interface ServiceManager {

    /**
     * Provides access to the UserService for managing user-related operations.
     *
     * @return an instance of UserService.
     */
    UserService getUserService();

    /**
     * Provides access to the ExpenseService for managing expense-related operations.
     *
     * @return an instance of ExpenseService.
     */
    ExpenseService getExpenseService();

    /**
     * Provides access to the CategoryService for managing category-related operations.
     *
     * @return an instance of CategoryService.
     */
    CategoryService getCategoryService();

    /**
     * Provides access to the StatisticsService for calculating user expense statistics.
     *
     * @return an instance of StatisticsService.
     */
    StatisticsService getStatisticsService();

    /**
     * Provides access to the ReportService for generating user expense reports.
     *
     * @return an instance of ReportService.
     */
    ReportService getReportService();
}
