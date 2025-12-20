package com.example.lab5.manual.functions.operations;

import com.example.lab5.manual.functions.exception.ValidationException;
import com.example.lab5.manual.functions.factory.TabulatedFunctionFactory;
import com.example.lab5.manual.functions.tabulated.TabulatedFunction;

/**
 * Дифференцирование табулированной функции методом конечных разностей.
 */
public class TabulatedDifferentialOperator {
    private final TabulatedFunctionFactory factory;

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunction differentiate(TabulatedFunction original) {
        if (original.getCount() < 2) {
            throw new ValidationException("Для дифференцирования требуется минимум две точки");
        }
        int count = original.getCount();
        double[] x = new double[count];
        double[] y = new double[count];
        for (int i = 0; i < count; i++) {
            x[i] = original.getX(i);
            y[i] = derivativeAt(original, i);
        }
        return factory.create(x, y);
    }

    private double derivativeAt(TabulatedFunction function, int index) {
        if (index == 0) {
            return slope(function, 0, 1);
        }
        if (index == function.getCount() - 1) {
            return slope(function, function.getCount() - 2, function.getCount() - 1);
        }
        return (function.getY(index + 1) - function.getY(index - 1)) /
                (function.getX(index + 1) - function.getX(index - 1));
    }

    private double slope(TabulatedFunction function, int left, int right) {
        double dx = function.getX(right) - function.getX(left);
        if (dx == 0.0) {
            throw new ValidationException("Невозможно вычислить производную при совпадающих X");
        }
        return (function.getY(right) - function.getY(left)) / dx;
    }
}
