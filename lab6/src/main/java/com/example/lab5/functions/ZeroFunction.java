package com.example.lab5.functions;

@FunctionProperties(nameLocale = "Нулевая функция", priority = 35)
public class ZeroFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return 0;
    }
}
