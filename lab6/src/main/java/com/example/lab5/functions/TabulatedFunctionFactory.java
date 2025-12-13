package com.example.lab5.functions;

import java.util.List;

public interface TabulatedFunctionFactory {
    TabulatedFunction createFromArrays(List<Point> points);

    TabulatedFunction createFromMathFunction(MathFunction sourceFunction, double start, double end, int count);
}
