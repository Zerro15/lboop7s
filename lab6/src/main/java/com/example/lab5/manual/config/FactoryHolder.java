package com.example.lab5.manual.config;

import com.example.lab5.manual.functions.factory.ArrayTabulatedFunctionFactory;
import com.example.lab5.manual.functions.factory.LinkedListTabulatedFunctionFactory;
import com.example.lab5.manual.functions.factory.TabulatedFunctionFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Хранит выбранную пользователем фабрику табулированных функций.
 */
public final class FactoryHolder {
    public enum FactoryType {
        ARRAY, LIST
    }

    private static final FactoryHolder INSTANCE = new FactoryHolder();

    private final AtomicReference<TabulatedFunctionFactory> currentFactory =
            new AtomicReference<>(new ArrayTabulatedFunctionFactory());
    private final AtomicReference<FactoryType> currentType = new AtomicReference<>(FactoryType.ARRAY);

    private FactoryHolder() {
    }

    public static FactoryHolder getInstance() {
        return INSTANCE;
    }

    public TabulatedFunctionFactory getFactory() {
        return currentFactory.get();
    }

    public FactoryType getType() {
        return currentType.get();
    }

    public void switchFactory(FactoryType type) {
        if (type == FactoryType.ARRAY) {
            currentFactory.set(new ArrayTabulatedFunctionFactory());
        } else {
            currentFactory.set(new LinkedListTabulatedFunctionFactory());
        }
        currentType.set(type);
    }
}
