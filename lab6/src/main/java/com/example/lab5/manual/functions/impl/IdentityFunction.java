package com.example.lab5.manual.functions.impl;

import com.example.lab5.manual.functions.MathFunction;

/**
 * Тождественная функция.
 */
public class IdentityFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x;
    }
}
