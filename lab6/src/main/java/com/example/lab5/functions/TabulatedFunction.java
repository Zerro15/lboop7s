package com.example.lab5.functions;

import java.util.List;
import java.util.Objects;

public class TabulatedFunction {
    private final List<Point> points;

    public TabulatedFunction(List<Point> points) {
        this.points = List.copyOf(points);
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TabulatedFunction that = (TabulatedFunction) o;
        return Objects.equals(points, that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }
}
