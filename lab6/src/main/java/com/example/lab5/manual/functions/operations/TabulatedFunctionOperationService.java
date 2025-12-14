package com.example.lab5.manual.functions.operations;

import com.example.lab5.manual.functions.exception.ValidationException;
import com.example.lab5.manual.functions.factory.TabulatedFunctionFactory;
import com.example.lab5.manual.functions.tabulated.TabulatedFunction;

/**
 * Поэлементные операции над табулированными функциями.
 */
public class TabulatedFunctionOperationService {
    private final TabulatedFunctionFactory factory;

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunction sum(TabulatedFunction first, TabulatedFunction second) {
        return operate(first, second, (a, b) -> a + b);
    }

    public TabulatedFunction subtract(TabulatedFunction first, TabulatedFunction second) {
        return operate(first, second, (a, b) -> a - b);
    }

    public TabulatedFunction multiply(TabulatedFunction first, TabulatedFunction second) {
        return operate(first, second, (a, b) -> a * b);
    }

    public TabulatedFunction divide(TabulatedFunction first, TabulatedFunction second) {
        return operate(first, second, (a, b) -> a / b);
    }

    private TabulatedFunction operate(TabulatedFunction first, TabulatedFunction second, DoubleBinaryOperator op) {
        validateCompatible(first, second);
        int count = first.getCount();
        double[] x = new double[count];
        double[] y = new double[count];
        for (int i = 0; i < count; i++) {
            x[i] = first.getX(i);
            y[i] = op.apply(first.getY(i), second.getY(i));
        }
        return factory.create(x, y);
    }

    private void validateCompatible(TabulatedFunction first, TabulatedFunction second) {
        if (first.getCount() != second.getCount()) {
            throw new ValidationException("Функции должны иметь одинаковое количество точек");
        }
        for (int i = 0; i < first.getCount(); i++) {
            if (Double.compare(first.getX(i), second.getX(i)) != 0) {
                throw new ValidationException("Значения X в функциях должны совпадать");
            }
        }
    }

    private interface DoubleBinaryOperator {
        double apply(double a, double b);
    }
}
