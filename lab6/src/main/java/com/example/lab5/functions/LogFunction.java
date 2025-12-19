package com.example.lab5.functions;

@FunctionProperties(nameLocale = "Натуральный логарифм", priority = 40)
public class LogFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x > 0 ? Math.log(x) : Double.NaN;
    }
}
