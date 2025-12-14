package com.example.lab5.manual.functions.impl;

import com.example.lab5.manual.functions.MathFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Составная функция, представляющая композицию нескольких функций.
 */
public class CompositeFunction implements MathFunction {
    private final List<MathFunction> chain;

    public CompositeFunction(List<MathFunction> chain) {
        if (chain == null || chain.isEmpty()) {
            throw new IllegalArgumentException("Цепочка функций пуста");
        }
        this.chain = Collections.unmodifiableList(new ArrayList<>(chain));
    }

    @Override
    public double apply(double x) {
        double result = x;
        for (int i = chain.size() - 1; i >= 0; i--) {
            MathFunction current = chain.get(i);
            Objects.requireNonNull(current, "Функция не задана");
            result = current.apply(result);
        }
        return result;
    }

    public List<MathFunction> getChain() {
        return chain;
    }
}
