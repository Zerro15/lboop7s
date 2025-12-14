package com.example.lab5.framework.math.functions;

import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class AnnotatedMathFunction {
    private final MathFunctionDescriptor descriptor;
    private final SimpleMathFunction function;

    public AnnotatedMathFunction(MathFunctionDescriptor descriptor, SimpleMathFunction function) {
        this.descriptor = descriptor;
        this.function = function;
    }

    public MathFunctionDescriptor getDescriptor() {
        return descriptor;
    }

    public SimpleMathFunction getFunction() {
        return function;
    }

    private static final AtomicReference<List<AnnotatedMathFunction>> CACHE = new AtomicReference<>();

    public static List<AnnotatedMathFunction> discoveryCache() {
        if (CACHE.get() == null) {
            CACHE.set(discoverAnnotatedFunctions());
        }
        return CACHE.get();
    }

    private static List<AnnotatedMathFunction> discoverAnnotatedFunctions() {
        Reflections reflections = new Reflections("com.example.lab5.framework.math.functions");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(MathFunctionDescriptor.class);
        List<AnnotatedMathFunction> result = new ArrayList<>();
        for (Class<?> clazz : annotated) {
            if (!SimpleMathFunction.class.isAssignableFrom(clazz)) {
                continue;
            }
            try {
                SimpleMathFunction function = (SimpleMathFunction) clazz.getDeclaredConstructor().newInstance();
                MathFunctionDescriptor descriptor = clazz.getAnnotation(MathFunctionDescriptor.class);
                result.add(new AnnotatedMathFunction(descriptor, function));
            } catch (Exception ignored) {
                // пропускаем некорректные классы
            }
        }
        return result;
    }
}
