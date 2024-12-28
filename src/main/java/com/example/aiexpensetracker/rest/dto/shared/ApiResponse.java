package com.example.aiexpensetracker.rest.dto.shared;

import java.time.LocalDateTime;

public class ApiResponse<T> {

    private LocalDateTime time;
    private int statusCode;
    private String message;
    private T data;

    // Constructors
    public ApiResponse() {
        // no-arg constructor for frameworks
    }

    public ApiResponse(LocalDateTime time, int statusCode, String message, T data) {
        this.time = time;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    // Getters & setters
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

