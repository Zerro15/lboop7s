package com.example.lab5.framework.math.functions;

import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;

@MathFunctionDescriptor(
        key = "cos",
        localizedName = "Косинус",
        description = "Тригонометрическая функция косинус",
        example = "f(x) = cos(x)",
        category = "тригонометрические",
        priority = 31
)
public class CosFunction implements SimpleMathFunction {
    @Override
    public double apply(double x) {
        return Math.cos(x);
    }
}
