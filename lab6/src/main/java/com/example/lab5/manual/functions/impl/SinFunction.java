package com.example.lab5.manual.functions.impl;

import com.example.lab5.manual.functions.MathFunction;

/**
 * Синусоидальная функция.
 */
public class SinFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.sin(x);
    }
}
