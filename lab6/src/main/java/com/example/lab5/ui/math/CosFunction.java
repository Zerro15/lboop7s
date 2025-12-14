package com.example.lab5.ui.math;

public class CosFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.cos(x);
    }

    @Override
    public String getKey() {
        return "cos";
    }

    @Override
    public String getLocalizedName() {
        return "Косинус";
    }
}
