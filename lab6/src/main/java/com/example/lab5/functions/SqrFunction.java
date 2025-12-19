package com.example.lab5.functions;

@FunctionProperties(nameLocale = "Квадратичная функция", priority = 10)
public class SqrFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x * x;
    }
}
