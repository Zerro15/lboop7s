package com.example.lab5.manual.functions.tabulated;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Простая реализация табулированной функции.
 */
public class TabulatedFunction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<TabulatedPoint> points;

    public TabulatedFunction(List<TabulatedPoint> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Список точек не может быть пустым");
        }
        this.points = new ArrayList<>(points);
    }

    public int getCount() {
        return points.size();
    }

    public double getX(int index) {
        return points.get(index).getX();
    }

    public double getY(int index) {
        return points.get(index).getY();
    }

    public void setY(int index, double value) {
        points.get(index).setY(value);
    }

    public List<TabulatedPoint> getPoints() {
        return Collections.unmodifiableList(points);
    }
}
