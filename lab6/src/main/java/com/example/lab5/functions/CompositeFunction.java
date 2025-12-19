package com.example.lab5.functions;

public class CompositeFunction implements MathFunction {

    private final MathFunction outer;
    private final MathFunction inner;

    public CompositeFunction(MathFunction outer, MathFunction inner) {
        if (outer == null || inner == null) {
            throw new IllegalArgumentException("Функции для композиции не заданы");
        }
        this.outer = outer;
        this.inner = inner;
    }

    @Override
    public double apply(double x) {
        return outer.apply(inner.apply(x));
    }

    public MathFunction getOuter() {
        return outer;
    }

    public MathFunction getInner() {
        return inner;
    }
}
