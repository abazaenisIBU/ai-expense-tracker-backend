package com.example.aiexpensetracker.core.service.manager;

import com.example.aiexpensetracker.core.service.category.CategoryService;
import com.example.aiexpensetracker.core.service.expense.ExpenseService;
import com.example.aiexpensetracker.core.service.report.ReportService;
import com.example.aiexpensetracker.core.service.statistics.StatisticsService;
import com.example.aiexpensetracker.core.service.user.UserService;
import org.springframework.stereotype.Component;

@Component
public class ServiceManagerImpl implements ServiceManager {
    private final UserService userService;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final StatisticsService statisticsService;
    private final ReportService reportService;

    public ServiceManagerImpl(
            UserService userService, ExpenseService expenseService, CategoryService categoryService, StatisticsService statisticsService, ReportService reportService){
        this.userService = userService;
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.statisticsService = statisticsService;
        this.reportService = reportService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }

    @Override
    public ExpenseService getExpenseService() {
        return expenseService;
    }

    @Override
    public CategoryService getCategoryService() {
        return categoryService;
    }

    @Override
    public StatisticsService getStatisticsService() {
        return statisticsService;
    }

    @Override
    public ReportService getReportService() {
        return reportService;
    }
}
