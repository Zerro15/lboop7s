package com.example.lab5.functions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultTabulatedFunctionFactory implements TabulatedFunctionFactory {
    @Override
    public TabulatedFunction createFromArrays(List<Point> points) {
        validatePoints(points);
        List<Point> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.comparingDouble(Point::x));
        return new TabulatedFunction(sorted);
    }

    @Override
    public TabulatedFunction createFromMathFunction(MathFunction sourceFunction, double start, double end, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Количество точек должно быть положительным");
        }
        if (Double.isNaN(start) || Double.isNaN(end)) {
            throw new IllegalArgumentException("Границы интервала должны быть числами");
        }
        if (start >= end) {
            throw new IllegalArgumentException("Начало интервала должно быть меньше конца");
        }

        double step = (end - start) / (count - 1);
        List<Point> generated = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            double x = start + step * i;
            double y = sourceFunction.apply(x);
            generated.add(new Point(x, y));
        }
        return new TabulatedFunction(generated);
    }

    private void validatePoints(List<Point> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Не переданы точки для табулирования");
        }
        Set<Double> xs = new HashSet<>();
        for (Point p : points) {
            if (p == null) {
                throw new IllegalArgumentException("Точки не должны быть пустыми");
            }
            if (Double.isNaN(p.x()) || Double.isNaN(p.y())) {
                throw new IllegalArgumentException("Значения X и Y должны быть числами");
            }
            if (!xs.add(p.x())) {
                throw new IllegalArgumentException("Значения X должны быть уникальными");
            }
        }
    }
}
