package com.example.lab5.functions;

import java.util.Arrays;

public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {

    @Override
    public TabulatedFunction create(MathFunction source, double xFrom, double xTo, int count) {
        if (source == null) {
            throw new IllegalArgumentException("Математическая функция не задана");
        }
        if (count < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (xFrom >= xTo) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        double[] xValues = new double[count];
        double[] yValues = new double[count];
        double step = (xTo - xFrom) / (count - 1);

        for (int i = 0; i < count; i++) {
            double x = xFrom + i * step;
            xValues[i] = x;
            yValues[i] = source.apply(x);
        }

        return new TabulatedFunction(xValues, yValues);
    }

    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        if (xValues == null || yValues == null) {
            throw new IllegalArgumentException("Массивы X и Y должны быть заданы");
        }
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Количество X и Y должно совпадать");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Точек должно быть не менее двух");
        }
        double[] sortedX = Arrays.copyOf(xValues, xValues.length);
        double[] sortedY = Arrays.copyOf(yValues, yValues.length);
        for (int i = 1; i < sortedX.length; i++) {
            if (sortedX[i] <= sortedX[i - 1]) {
                throw new IllegalArgumentException("Массив X должен быть строго возрастающим");
            }
        }
        return new TabulatedFunction(sortedX, sortedY);
    }
}
