package com.example.lab5.manual.functions.exception;

/**
 * Исключение, когда последовательность X нестрого возрастающая.
 */
public class NonIncreasingSequenceException extends ValidationException {
    public NonIncreasingSequenceException() {
        super("Значения X должны быть строго возрастающими");
    }
}
