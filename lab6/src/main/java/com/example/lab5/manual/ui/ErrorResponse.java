package com.example.lab5.manual.ui;

/**
 * DTO ошибки для фронтенда.
 */
public class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
