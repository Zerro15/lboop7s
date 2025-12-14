package com.example.lab5.manual.functions;

import com.example.lab5.manual.functions.impl.IdentityFunction;
import com.example.lab5.manual.functions.impl.SinFunction;
import com.example.lab5.manual.functions.impl.SqrFunction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Реестр доступных математических функций с локализованными именами.
 */
public final class MathFunctionRegistry {
    private final Map<String, MathFunction> functions = new LinkedHashMap<>();

    public MathFunctionRegistry() {
        Map<String, MathFunction> sorted = new TreeMap<>();
        sorted.put("Квадратичная функция", new SqrFunction());
        sorted.put("Синусоидальная функция", new SinFunction());
        sorted.put("Тождественная функция", new IdentityFunction());
        functions.putAll(sorted);
    }

    public Map<String, MathFunction> getFunctions() {
        return functions;
    }
}
