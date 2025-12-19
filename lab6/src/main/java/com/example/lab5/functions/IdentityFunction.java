package com.example.lab5.functions;

@FunctionProperties(nameLocale = "Тождественная функция", priority = 5)
public class IdentityFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x;
    }
}
