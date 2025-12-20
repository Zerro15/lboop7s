package com.example.lab5.manual.functions;

/**
 * Базовый интерфейс математической функции.
 */
@FunctionalInterface
public interface MathFunction {
    double apply(double x);
}
