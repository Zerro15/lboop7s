package com.example.lab5.manual.functions.factory;

import com.example.lab5.manual.functions.MathFunction;
import com.example.lab5.manual.functions.tabulated.TabulatedFunction;

/**
 * Фабрика для создания табулированных функций.
 */
public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);

    TabulatedFunction create(MathFunction function, double from, double to, int count);
}
