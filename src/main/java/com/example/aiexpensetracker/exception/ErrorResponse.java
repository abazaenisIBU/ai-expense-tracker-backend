package com.example.aiexpensetracker.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private int status;
    private String error;
    private List<ErrorDetail> details;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, List<ErrorDetail> details) {
        this.status = status;
        this.error = error;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public static class ErrorDetail {
        private String field;
        private String message;

        public ErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}
