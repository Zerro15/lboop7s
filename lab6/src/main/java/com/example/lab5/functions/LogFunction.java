package com.example.lab5.functions;

public class LogFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x > 0 ? Math.log(x) : Double.NaN;
    }
}
