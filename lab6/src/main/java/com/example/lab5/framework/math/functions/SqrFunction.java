package com.example.lab5.framework.math.functions;

import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;

@MathFunctionDescriptor(
        key = "sqr",
        localizedName = "Квадратичная функция",
        description = "Функция y = x²",
        example = "f(x) = x²",
        category = "алгебраические",
        priority = 20
)
public class SqrFunction implements SimpleMathFunction {
    @Override
    public double apply(double x) {
        return x * x;
    }
}
