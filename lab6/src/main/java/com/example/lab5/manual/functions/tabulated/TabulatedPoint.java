package com.example.lab5.manual.functions.tabulated;

import java.io.Serializable;

/**
 * Точка табулированной функции.
 */
public class TabulatedPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private double x;
    private double y;

    public TabulatedPoint() {
    }

    public TabulatedPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
