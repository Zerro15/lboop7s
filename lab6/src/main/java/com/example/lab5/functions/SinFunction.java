package com.example.lab5.functions;

@FunctionProperties(nameLocale = "Синусоида", priority = 20)
public class SinFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.sin(x);
    }
}
