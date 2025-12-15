package com.example.lab5.framework.dto;

public class OperationResponse {
    private TabulatedFunctionPayload result;

    // Конструктор, который принимает только TabulatedFunctionPayload
    public OperationResponse(TabulatedFunctionPayload result) {
        this.result = result;
    }

    // Конструктор без параметров (по умолчанию)
    public OperationResponse() {}

    // Геттер
    public TabulatedFunctionPayload getResult() {
        return result;
    }

    // Сеттер
    public void setResult(TabulatedFunctionPayload result) {
        this.result = result;
    }
}
