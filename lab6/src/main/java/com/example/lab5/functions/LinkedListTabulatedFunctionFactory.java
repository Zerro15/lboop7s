package com.example.lab5.functions;

import java.util.LinkedList;
import java.util.List;

public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {

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

        List<Double> xValues = new LinkedList<>();
        List<Double> yValues = new LinkedList<>();
        double step = (xTo - xFrom) / (count - 1);

        for (int i = 0; i < count; i++) {
            double x = xFrom + i * step;
            xValues.add(x);
            yValues.add(source.apply(x));
        }

        return new TabulatedFunction(toArray(xValues), toArray(yValues));
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
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new IllegalArgumentException("Массив X должен быть строго возрастающим");
            }
        }
        return new TabulatedFunction(xValues, yValues);
    }

    private double[] toArray(List<Double> values) {
        double[] array = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = values.get(i);
        }
        return array;
    }
}
