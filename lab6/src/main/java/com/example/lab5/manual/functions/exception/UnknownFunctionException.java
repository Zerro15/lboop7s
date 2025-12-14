package com.example.lab5.manual.functions.exception;

/**
 * Исключение, когда выбранная функция не найдена.
 */
public class UnknownFunctionException extends ValidationException {
    public UnknownFunctionException(String displayName) {
        super("Функция '" + displayName + "' не найдена");
    }
}
