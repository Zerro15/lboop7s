package com.example.lab5.manual.functions.tabulated;

import java.util.Collections;
import java.util.List;

/**
 * Простая реализация табулированной функции.
 */
public class TabulatedFunction {
    private final List<TabulatedPoint> points;

    public TabulatedFunction(List<TabulatedPoint> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Список точек не может быть пустым");
        }
        this.points = List.copyOf(points);
    }

    public List<TabulatedPoint> getPoints() {
        return Collections.unmodifiableList(points);
    }
}
