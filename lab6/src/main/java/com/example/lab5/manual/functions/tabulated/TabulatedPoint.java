package com.example.lab5.manual.functions.tabulated;

import java.io.Serializable;

/**
 * Точка табулированной функции.
 */
public class TabulatedPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double x;
    private double y;

    public TabulatedPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
