package com.example.lab5.framework.math.functions;

import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;

@MathFunctionDescriptor(
        key = "sin",
        localizedName = "Синус",
        description = "Тригонометрическая функция синус",
        example = "f(x) = sin(x)",
        category = "тригонометрические",
        priority = 30
)
public class SinFunction implements SimpleMathFunction {
    @Override
    public double apply(double x) {
        return Math.sin(x);
    }
}
