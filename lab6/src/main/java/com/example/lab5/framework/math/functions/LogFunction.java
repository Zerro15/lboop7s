package com.example.lab5.framework.math.functions;

import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;

@MathFunctionDescriptor(
        key = "log",
        localizedName = "Натуральный логарифм",
        description = "Логарифмическая функция",
        example = "f(x) = ln(x)",
        category = "логарифмические",
        priority = 50
)
public class LogFunction implements SimpleMathFunction {
    @Override
    public double apply(double x) {
        return x > 0 ? Math.log(x) : Double.NaN;
    }
}
