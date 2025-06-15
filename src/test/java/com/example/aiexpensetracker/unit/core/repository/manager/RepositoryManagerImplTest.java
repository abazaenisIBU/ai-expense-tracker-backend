package com.example.aiexpensetracker.unit.core.repository.manager;

import com.example.aiexpensetracker.core.repository.category.CategoryRepository;
import com.example.aiexpensetracker.core.repository.expense.ExpenseRepository;
import com.example.aiexpensetracker.core.repository.manager.RepositoryManagerImpl;
import com.example.aiexpensetracker.core.repository.user.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RepositoryManagerImplTest {

    @Test
    void constructorAndGetters_shouldReturnInjectedRepositories() {
        // Arrange
        UserRepository mockUserRepo = mock(UserRepository.class);
        ExpenseRepository mockExpenseRepo = mock(ExpenseRepository.class);
        CategoryRepository mockCategoryRepo = mock(CategoryRepository.class);

        // Act
        RepositoryManagerImpl manager =
                new RepositoryManagerImpl(mockUserRepo, mockExpenseRepo, mockCategoryRepo);

        // Assert
        assertSame(mockUserRepo, manager.getUserRepository(), "getUserRepository() should return the injected UserRepository");
        assertSame(mockExpenseRepo, manager.getExpenseRepository(), "getExpenseRepository() should return the injected ExpenseRepository");
        assertSame(mockCategoryRepo, manager.getCategoryRepository(), "getCategoryRepository() should return the injected CategoryRepository");
    }
}