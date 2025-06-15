package com.example.aiexpensetracker.exception.category;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryNotFoundExceptionTest {

    @Test
    void whenConstructedWithMessage_thenMessageIsStored() {
        String expected = "Category with ID 42 not found";
        CategoryNotFoundException ex = new CategoryNotFoundException(expected);

        assertEquals(expected, ex.getMessage(),
                "Constructor message should be returned by getMessage()");
    }
}