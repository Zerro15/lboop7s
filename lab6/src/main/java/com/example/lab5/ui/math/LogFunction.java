package com.example.lab5.ui.math;

public class LogFunction implements MathFunction {
    @Override
    public double apply(double x) {
        if (x <= 0) {
            return Double.NaN;
        }
        return Math.log(x);
    }

    @Override
    public String getKey() {
        return "log";
    }

    @Override
    public String getLocalizedName() {
        return "Натуральный логарифм";
    }
}
