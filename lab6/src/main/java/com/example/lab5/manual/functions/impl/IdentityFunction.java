package com.example.lab5.manual.functions.impl;

import com.example.lab5.manual.functions.MathFunction;
import com.example.lab5.manual.functions.MathFunctionInfo;

/**
 * Тождественная функция.
 */
@MathFunctionInfo(name = "Тождественная функция", priority = 2)
public class IdentityFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x;
    }
}
