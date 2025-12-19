package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.IntegrationRequest;
import com.example.lab5.framework.dto.IntegrationResponse;
import com.example.lab5.framework.dto.TabulatedFunctionPayload;
import com.example.lab5.functions.TabulatedFunction;
import com.example.lab5.functions.TabulatedFunctionFactory;
import com.example.lab5.functions.TabulatedIntegralCalculator;
import org.springframework.stereotype.Service;

@Service
public class TabulatedIntegrationService {

    private final TabulatedFunctionFactoryHolder factoryHolder;
    private final TabulatedIntegralCalculator calculator = new TabulatedIntegralCalculator();

    public TabulatedIntegrationService(TabulatedFunctionFactoryHolder factoryHolder) {
        this.factoryHolder = factoryHolder;
    }

    public IntegrationResponse integrate(IntegrationRequest request) {
        if (request == null || request.getFunction() == null) {
            throw new IllegalArgumentException("Функция для интегрирования должна быть задана");
        }
        TabulatedFunctionFactory factory = factoryHolder.resolveFactory(request.getFactoryType());
        TabulatedFunction source = toFunction(factory, request.getFunction());
        double result = calculator.integrate(source, request.getThreads());
        String factoryKey = factoryHolder.resolveKey(request.getFactoryType());
        return new IntegrationResponse(result, factoryKey);
    }

    private TabulatedFunction toFunction(TabulatedFunctionFactory factory, TabulatedFunctionPayload payload) {
        if (payload.getXValues() == null || payload.getYValues() == null) {
            throw new IllegalArgumentException("Значения функции должны быть заданы полностью");
        }
        return factory.create(payload.getXValues(), payload.getYValues());
    }
}
