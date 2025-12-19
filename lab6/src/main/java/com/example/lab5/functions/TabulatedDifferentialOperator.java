package com.example.lab5.functions;

public class TabulatedDifferentialOperator {
    private final TabulatedFunctionFactory factory;

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Фабрика не должна быть null");
        }
        this.factory = factory;
    }

    public TabulatedFunction derive(TabulatedFunction function) {
        if (function == null) {
            throw new IllegalArgumentException("Исходная функция должна быть задана");
        }
        if (function.size() < 2) {
            throw new IllegalArgumentException("Для дифференцирования требуется минимум две точки");
        }

        double[] xValues = function.getXValues();
        double[] derivativeY = new double[xValues.length];

        for (int i = 0; i < xValues.length; i++) {
            if (i == 0) {
                derivativeY[i] = slope(function, i, i + 1);
            } else if (i == xValues.length - 1) {
                derivativeY[i] = slope(function, i - 1, i);
            } else {
                derivativeY[i] = slope(function, i - 1, i + 1);
            }
        }

        return factory.create(xValues, derivativeY);
    }

    private double slope(TabulatedFunction function, int leftIndex, int rightIndex) {
        double dx = function.getX(rightIndex) - function.getX(leftIndex);
        if (Math.abs(dx) < 1e-12) {
            throw new IllegalArgumentException("Разность X слишком мала для вычисления производной");
        }
        double dy = function.getY(rightIndex) - function.getY(leftIndex);
        return dy / dx;
    }
}
