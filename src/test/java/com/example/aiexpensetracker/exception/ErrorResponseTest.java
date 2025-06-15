package com.example.aiexpensetracker.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void constructorShouldInitializeAllFieldsAndTimestamp() {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        ErrorResponse.ErrorDetail detail1 =
                new ErrorResponse.ErrorDetail("username", "must not be blank");
        ErrorResponse.ErrorDetail detail2 =
                new ErrorResponse.ErrorDetail("email", "invalid format");
        List<ErrorResponse.ErrorDetail> details = Arrays.asList(detail1, detail2);

        int expectedStatus = 400;
        String expectedError = "Bad Request";

        // Act
        ErrorResponse response = new ErrorResponse(expectedStatus, expectedError, details);

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // Assert
        assertEquals(expectedStatus, response.getStatus(), "Status should match constructor input");
        assertEquals(expectedError, response.getError(), "Error message should match constructor input");
        assertSame(details, response.getDetails(), "Details list should be stored, not copied");

        LocalDateTime timestamp = response.getTimestamp();
        assertNotNull(timestamp, "Timestamp must not be null");
        assertTrue(!timestamp.isBefore(before) && !timestamp.isAfter(after),
                "Timestamp should be between before and after markers");

        ErrorResponse.ErrorDetail got1 = response.getDetails().getFirst();
        assertEquals("username", got1.getField());
        assertEquals("must not be blank", got1.getMessage());

        ErrorResponse.ErrorDetail got2 = response.getDetails().get(1);
        assertEquals("email", got2.getField());
        assertEquals("invalid format", got2.getMessage());
    }
}