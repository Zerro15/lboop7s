package com.example.lab5.framework.math.functions;

import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;

@MathFunctionDescriptor(
        key = "identity",
        localizedName = "Тождественная функция",
        description = "Функция y = x",
        example = "f(x) = x",
        category = "алгебраические",
        priority = 10
)
public class IdentityFunction implements SimpleMathFunction {
    @Override
    public double apply(double x) {
        return x;
    }
}
