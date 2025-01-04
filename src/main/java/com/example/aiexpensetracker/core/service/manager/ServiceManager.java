package com.example.aiexpensetracker.core.service.manager;

import com.example.aiexpensetracker.core.service.category.CategoryService;
import com.example.aiexpensetracker.core.service.expense.ExpenseService;
import com.example.aiexpensetracker.core.service.report.ReportService;
import com.example.aiexpensetracker.core.service.statistics.StatisticsService;
import com.example.aiexpensetracker.core.service.user.UserService;

public interface ServiceManager {
    UserService getUserService();

    ExpenseService getExpenseService();

    CategoryService getCategoryService();

    StatisticsService getStatisticsService();

    ReportService getReportService();
}