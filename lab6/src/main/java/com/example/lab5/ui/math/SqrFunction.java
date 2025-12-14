package com.example.lab5.ui.math;

public class SqrFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x * x;
    }

    @Override
    public String getKey() {
        return "sqr";
    }

    @Override
    public String getLocalizedName() {
        return "Квадратичная функция";
    }
}
