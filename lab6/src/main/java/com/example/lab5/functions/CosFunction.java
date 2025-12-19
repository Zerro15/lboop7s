package com.example.lab5.functions;

@FunctionProperties(nameLocale = "Косинус", priority = 25)
public class CosFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.cos(x);
    }
}
