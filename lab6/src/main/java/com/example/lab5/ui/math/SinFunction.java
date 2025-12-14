package com.example.lab5.ui.math;

public class SinFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.sin(x);
    }

    @Override
    public String getKey() {
        return "sin";
    }

    @Override
    public String getLocalizedName() {
        return "Синус";
    }
}
