package com.example.lab5.manual.functions.impl;

import com.example.lab5.manual.functions.MathFunction;
import com.example.lab5.manual.functions.MathFunctionInfo;

/**
 * Синусоидальная функция.
 */
@MathFunctionInfo(name = "Синусоидальная функция", priority = 1)
public class SinFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.sin(x);
    }
}
