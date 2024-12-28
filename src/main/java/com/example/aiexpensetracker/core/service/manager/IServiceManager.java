package com.example.aiexpensetracker.core.service.manager;

import com.example.aiexpensetracker.core.service.category.ICategoryService;
import com.example.aiexpensetracker.core.service.expense.IExpenseService;
import com.example.aiexpensetracker.core.service.report.IReportService;
import com.example.aiexpensetracker.core.service.statistics.IStatisticsService;
import com.example.aiexpensetracker.core.service.user.IUserService;

public interface IServiceManager {
    IUserService getUserService();

    IExpenseService getExpenseService();

    ICategoryService getCategoryService();

    IStatisticsService getStatisticsService();

    IReportService getReportService();
}