package com.example.lab5.manual.functions.tabulated;

import com.example.lab5.manual.functions.exception.ValidationException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Простая реализация табулированной функции.
 */
public class TabulatedFunction implements Serializable, Insertable, Removable {
    private static final long serialVersionUID = 1L;

    private List<TabulatedPoint> points;

    public TabulatedFunction() {
        this.points = new ArrayList<>();
    }

    public TabulatedFunction(List<TabulatedPoint> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Список точек не может быть пустым");
        }
        this.points = new ArrayList<>(points);
        ensureStrictIncreasing();
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

    public void setPoints(List<TabulatedPoint> points) {
        if (points == null || points.isEmpty()) {
            throw new ValidationException("Список точек не может быть пустым");
        }
        this.points = new ArrayList<>(points);
        ensureStrictIncreasing();
    }

    /**
     * Линейная интерполяция/экстраполяция значения функции.
     */
    public double apply(double x) {
        if (points.isEmpty()) {
            throw new ValidationException("Функция не содержит точек");
        }
        if (points.size() == 1) {
            return points.get(0).getY();
        }
        if (x <= points.get(0).getX()) {
            return interpolate(points.get(0), points.get(1), x);
        }
        if (x >= points.get(points.size() - 1).getX()) {
            return interpolate(points.get(points.size() - 2), points.get(points.size() - 1), x);
        }
        for (int i = 0; i < points.size() - 1; i++) {
            TabulatedPoint left = points.get(i);
            TabulatedPoint right = points.get(i + 1);
            if (x >= left.getX() && x <= right.getX()) {
                return interpolate(left, right, x);
            }
        }
        throw new ValidationException("Невозможно интерполировать значение");
    }

    private double interpolate(TabulatedPoint left, TabulatedPoint right, double x) {
        double dx = right.getX() - left.getX();
        if (dx == 0) {
            return left.getY();
        }
        double dy = right.getY() - left.getY();
        double factor = (x - left.getX()) / dx;
        return left.getY() + dy * factor;
    }

    @Override
    public void insert(double x, double y) {
        TabulatedPoint newPoint = new TabulatedPoint(x, y);
        points.add(newPoint);
        points.sort(Comparator.comparingDouble(TabulatedPoint::getX));
        ensureStrictIncreasing();
    }

    @Override
    public void remove(int index) {
        if (points.size() <= 1) {
            throw new ValidationException("Нельзя удалить последнюю точку");
        }
        if (index < 0 || index >= points.size()) {
            throw new ValidationException("Индекс вне диапазона");
        }
        points.remove(index);
    }

    private void ensureStrictIncreasing() {
        double prev = points.get(0).getX();
        for (int i = 1; i < points.size(); i++) {
            double current = points.get(i).getX();
            if (current == prev) {
                throw new ValidationException("Дублирующее значение X: " + current);
            }
            prev = current;
        }
    }
}
