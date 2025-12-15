package com.example.lab5.functions;

public class ExpFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.exp(x);
    }
}
