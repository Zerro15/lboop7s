package com.example.lab5.ui.math;

public class ExpFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.exp(x);
    }

    @Override
    public String getKey() {
        return "exp";
    }

    @Override
    public String getLocalizedName() {
        return "Экспонента";
    }
}
