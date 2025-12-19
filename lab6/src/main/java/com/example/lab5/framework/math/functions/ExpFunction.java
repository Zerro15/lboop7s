package com.example.lab5.framework.math.functions;

import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;

@MathFunctionDescriptor(
        key = "exp",
        localizedName = "Экспонента",
        description = "Экспоненциальная функция",
        example = "f(x) = e^x",
        category = "экспоненциальные",
        priority = 40
)
public class ExpFunction implements SimpleMathFunction {
    @Override
    public double apply(double x) {
        return Math.exp(x);
    }
}
