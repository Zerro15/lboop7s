package com.example.lab5.manual.functions.exception;

/**
 * Базовое исключение валидации пользовательского ввода.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
