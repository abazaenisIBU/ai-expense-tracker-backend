package com.example.aiexpensetracker.core.service.manager;

import com.example.aiexpensetracker.core.service.category.CategoryService;
import com.example.aiexpensetracker.core.service.expense.ExpenseService;
import com.example.aiexpensetracker.core.service.report.ReportService;
import com.example.aiexpensetracker.core.service.statistics.StatisticsService;
import com.example.aiexpensetracker.core.service.user.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ServiceManagerImplTest {

    @Test
    void constructorAndGetters_shouldReturnInjectedServices() {
        // Arrange
        UserService mockUserSvc = mock(UserService.class);
        ExpenseService mockExpenseSvc = mock(ExpenseService.class);
        CategoryService mockCategorySvc = mock(CategoryService.class);
        StatisticsService mockStatsSvc = mock(StatisticsService.class);
        ReportService mockReportSvc = mock(ReportService.class);

        // Act
        ServiceManagerImpl manager = new ServiceManagerImpl(
                mockUserSvc, mockExpenseSvc, mockCategorySvc, mockStatsSvc, mockReportSvc
        );

        // Assert
        assertSame(mockUserSvc,       manager.getUserService(),       "getUserService() should return the injected UserService");
        assertSame(mockExpenseSvc,    manager.getExpenseService(),    "getExpenseService() should return the injected ExpenseService");
        assertSame(mockCategorySvc,   manager.getCategoryService(),   "getCategoryService() should return the injected CategoryService");
        assertSame(mockStatsSvc,      manager.getStatisticsService(), "getStatisticsService() should return the injected StatisticsService");
        assertSame(mockReportSvc,     manager.getReportService(),     "getReportService() should return the injected ReportService");
    }
}