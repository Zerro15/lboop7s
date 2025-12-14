package com.example.lab5.manual.functions.impl;

import com.example.lab5.manual.functions.MathFunction;

/**
 * Квадратичная функция.
 */
public class SqrFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x * x;
    }
}
