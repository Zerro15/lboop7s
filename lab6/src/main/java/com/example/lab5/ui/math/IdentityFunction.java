package com.example.lab5.ui.math;

public class IdentityFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x;
    }

    @Override
    public String getKey() {
        return "identity";
    }

    @Override
    public String getLocalizedName() {
        return "Тождественная функция";
    }
}
