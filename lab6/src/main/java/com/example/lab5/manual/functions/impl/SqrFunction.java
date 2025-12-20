package com.example.lab5.manual.functions.impl;

import com.example.lab5.manual.functions.MathFunction;
import com.example.lab5.manual.functions.MathFunctionInfo;

/**
 * Квадратичная функция.
 */
@MathFunctionInfo(name = "Квадратичная функция", priority = 1)
public class SqrFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x * x;
    }
}
