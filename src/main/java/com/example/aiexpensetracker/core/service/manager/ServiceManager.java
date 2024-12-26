package com.example.aiexpensetracker.core.service.manager;

import com.example.aiexpensetracker.core.service.category.ICategoryService;
import com.example.aiexpensetracker.core.service.expense.IExpenseService;
import com.example.aiexpensetracker.core.service.report.IReportService;
import com.example.aiexpensetracker.core.service.statistics.IStatisticsService;
import com.example.aiexpensetracker.core.service.user.IUserService;
import org.springframework.stereotype.Component;

@Component
public class ServiceManager implements IServiceManager {
    private final IUserService userService;
    private final IExpenseService expenseService;
    private final ICategoryService categoryService;
    private final IStatisticsService statisticsService;
    private final IReportService reportService;

    public ServiceManager(
            IUserService userService, IExpenseService expenseService, ICategoryService categoryService, IStatisticsService statisticsService, IReportService reportService){
        this.userService = userService;
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.statisticsService = statisticsService;
        this.reportService = reportService;
    }

    @Override
    public IUserService getUserService() {
        return userService;
    }

    @Override
    public IExpenseService getExpenseService() {
        return expenseService;
    }

    @Override
    public ICategoryService getCategoryService() {
        return categoryService;
    }

    @Override
    public IStatisticsService getStatisticsService() {
        return statisticsService;
    }

    @Override
    public IReportService getReportService() {
        return reportService;
    }
}
