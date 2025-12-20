package com.example.lab5.manual.functions.tabulated;

import com.example.lab5.manual.functions.exception.ValidationException;
import com.example.lab5.manual.functions.factory.TabulatedFunctionFactory;

import java.util.List;

/**
 * Утилиты преобразования между DTO и табулированной функцией.
 */
public final class TabulatedFunctionMapper {
    private TabulatedFunctionMapper() {
    }

    public static TabulatedFunction fromPoints(List<TabulatedPoint> points, TabulatedFunctionFactory factory) {
        if (points == null || points.isEmpty()) {
            throw new ValidationException("Точки функции не заданы");
        }
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            x[i] = points.get(i).getX();
            y[i] = points.get(i).getY();
        }
        return factory.create(x, y);
    }
}
