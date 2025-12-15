package com.example.lab5.functions;

public class TabulatedFunctionOperationService {
    private final TabulatedFunctionFactory factory;

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Фабрика не должна быть null");
        }
        this.factory = factory;
    }

    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        return calculate(a, b, (y1, y2) -> y1 + y2);
    }

    public TabulatedFunction subtract(TabulatedFunction a, TabulatedFunction b) {
        return calculate(a, b, (y1, y2) -> y1 - y2);
    }

    public TabulatedFunction multiply(TabulatedFunction a, TabulatedFunction b) {
        return calculate(a, b, (y1, y2) -> y1 * y2);
    }

    public TabulatedFunction divide(TabulatedFunction a, TabulatedFunction b) {
        return calculate(a, b, (y1, y2) -> {
            if (Math.abs(y2) < 1e-12) {
                throw new IllegalArgumentException("Деление на ноль в точке");
            }
            return y1 / y2;
        });
    }

    private TabulatedFunction calculate(TabulatedFunction a, TabulatedFunction b, BinaryOperator op) {
        validateOperands(a, b);
        double[] x = a.getXValues();
        double[] resultY = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            resultY[i] = op.apply(a.getY(i), b.getY(i));
        }

        return factory.create(x, resultY);
    }

    private void validateOperands(TabulatedFunction a, TabulatedFunction b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Оба операнда должны быть заданы");
        }
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Количество точек функций должно совпадать");
        }
        for (int i = 0; i < a.size(); i++) {
            if (Math.abs(a.getX(i) - b.getX(i)) > 1e-9) {
                throw new IllegalArgumentException("X значений операндов должны совпадать");
            }
        }
    }

    @FunctionalInterface
    private interface BinaryOperator {
        double apply(double a, double b);
    }
}
