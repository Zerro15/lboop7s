package com.example.lab5.manual.exception;

/**
 * Исключение для ошибок аутентификации, требующее ответа 401.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
