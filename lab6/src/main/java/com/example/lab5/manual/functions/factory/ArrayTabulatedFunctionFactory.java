package com.example.lab5.manual.functions.factory;

import com.example.lab5.manual.functions.MathFunction;
import com.example.lab5.manual.functions.exception.NonIncreasingSequenceException;
import com.example.lab5.manual.functions.exception.TableSizeLimitExceededException;
import com.example.lab5.manual.functions.exception.ValidationException;
import com.example.lab5.manual.functions.tabulated.TabulatedFunction;
import com.example.lab5.manual.functions.tabulated.TabulatedPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика создания табулированных функций на основе массивов или произвольной функции.
 */
public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
    private static final int MAX_POINTS = 500;

    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        validateInput(xValues, yValues);
        return buildTabulatedFunction(xValues, yValues);
    }

    @Override
    public TabulatedFunction create(MathFunction function, double from, double to, int count) {
        if (count <= 0) {
            throw new ValidationException("Количество точек должно быть положительным");
        }
        if (count > MAX_POINTS) {
            throw new TableSizeLimitExceededException(MAX_POINTS);
        }
        if (Double.isNaN(from) || Double.isNaN(to)) {
            throw new ValidationException("Границы интервала должны быть числами");
        }
        if (from >= to) {
            throw new ValidationException("Левая граница должна быть меньше правой");
        }

        double step = (to - from) / (count - 1);
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for (int i = 0; i < count; i++) {
            double x = from + i * step;
            xValues[i] = x;
            yValues[i] = function.apply(x);
        }
        return buildTabulatedFunction(xValues, yValues);
    }

    private TabulatedFunction buildTabulatedFunction(double[] xValues, double[] yValues) {
        List<TabulatedPoint> points = new ArrayList<>(xValues.length);
        for (int i = 0; i < xValues.length; i++) {
            points.add(new TabulatedPoint(xValues[i], yValues[i]));
        }
        return new TabulatedFunction(points);
    }

    private void validateInput(double[] xValues, double[] yValues) {
        if (xValues == null || yValues == null) {
            throw new ValidationException("Массивы значений не могут быть null");
        }
        if (xValues.length != yValues.length) {
            throw new ValidationException("Количество X и Y должно совпадать");
        }
        if (xValues.length < 2) {
            throw new ValidationException("Нужно минимум две точки для табуляции");
        }
        if (xValues.length > MAX_POINTS) {
            throw new TableSizeLimitExceededException(MAX_POINTS);
        }

        double prev = xValues[0];
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= prev) {
                throw new NonIncreasingSequenceException();
            }
            prev = xValues[i];
        }
    }
}
