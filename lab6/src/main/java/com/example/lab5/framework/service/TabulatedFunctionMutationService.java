package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.TabulatedFunctionPayload;
import com.example.lab5.functions.Insertable;
import com.example.lab5.functions.Removable;
import com.example.lab5.functions.TabulatedFunction;
import com.example.lab5.functions.TabulatedFunctionFactory;
import org.springframework.stereotype.Service;

@Service
public class TabulatedFunctionMutationService {

    private final TabulatedFunctionFactoryHolder factoryHolder;

    public TabulatedFunctionMutationService(TabulatedFunctionFactoryHolder factoryHolder) {
        this.factoryHolder = factoryHolder;
    }

    public TabulatedFunctionPayload insert(TabulatedFunctionPayload payload, double x, double y, String factoryKey) {
        validatePayload(payload);
        TabulatedFunctionFactory factory = factoryHolder.resolveFactory(factoryKey);
        TabulatedFunction function = factory.create(payload.getXValues(), payload.getYValues());
        if (!(function instanceof Insertable)) {
            throw new IllegalArgumentException("Эта функция не поддерживает вставку точек");
        }

        ((Insertable) function).insert(x, y);
        return toPayload(payload.getName(), function, factoryKey);
    }

    public TabulatedFunctionPayload remove(TabulatedFunctionPayload payload, int index, String factoryKey) {
        validatePayload(payload);
        TabulatedFunctionFactory factory = factoryHolder.resolveFactory(factoryKey);
        TabulatedFunction function = factory.create(payload.getXValues(), payload.getYValues());
        if (!(function instanceof Removable)) {
            throw new IllegalArgumentException("Эта функция не поддерживает удаление точек");
        }

        ((Removable) function).remove(index);
        return toPayload(payload.getName(), function, factoryKey);
    }

    private TabulatedFunctionPayload toPayload(String name, TabulatedFunction function, String factoryKey) {
        TabulatedFunctionPayload response = new TabulatedFunctionPayload();
        response.setName(name);
        response.setXValues(function.getXValues());
        response.setYValues(function.getYValues());
        response.setFactoryType(factoryHolder.resolveKey(factoryKey));
        response.setInsertable(function instanceof Insertable);
        response.setRemovable(function instanceof Removable);
        return response;
    }

    private void validatePayload(TabulatedFunctionPayload payload) {
        if (payload == null || payload.getXValues() == null || payload.getYValues() == null) {
            throw new IllegalArgumentException("Функция должна содержать значения X и Y");
        }
    }
}
