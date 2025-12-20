package com.example.lab5.manual.functions.exception;

/**
 * Исключение при превышении допустимого размера таблицы.
 */
public class TableSizeLimitExceededException extends ValidationException {
    public TableSizeLimitExceededException(int limit) {
        super("Количество точек превышает допустимый предел " + limit);
    }
}
