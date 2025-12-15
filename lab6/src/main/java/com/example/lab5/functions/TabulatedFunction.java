package com.example.lab5.functions;

public class TabulatedFunction {
    private final double[] xValues;
    private final double[] yValues;

    public TabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Количество X и Y должно совпадать");
        }
        this.xValues = xValues;
        this.yValues = yValues;
    }

    public double[] getXValues() {
        return xValues;
    }

    public double[] getYValues() {
        return yValues;
    }
}
