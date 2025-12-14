package com.example.lab5.ui.exception;

import java.time.Instant;

public class ErrorResponse {
    private final int status;
    private final String message;
    private final Instant timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
