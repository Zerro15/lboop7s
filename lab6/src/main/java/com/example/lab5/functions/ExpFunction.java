package com.example.lab5.functions;

@FunctionProperties(nameLocale = "Экспонента", priority = 30)
public class ExpFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.exp(x);
    }
}
