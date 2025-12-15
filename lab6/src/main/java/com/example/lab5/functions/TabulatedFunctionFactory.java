package com.example.lab5.functions;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(MathFunction source, double xFrom, double xTo, int count);
    TabulatedFunction create(double[] xValues, double[] yValues);
}