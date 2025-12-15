package com.example.lab5.framework.dto;

public class OperationRequest {
    private String operation; // add, subtract, multiply, divide
    private String factoryType;
    private TabulatedFunctionPayload left;
    private TabulatedFunctionPayload right;

    // Конструкторы
    public OperationRequest() {}

    public OperationRequest(String operation, String factoryType, TabulatedFunctionPayload left, TabulatedFunctionPayload right) {
        this.operation = operation;
        this.factoryType = factoryType;
        this.left = left;
        this.right = right;
    }

    // Геттеры и сеттеры
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }

    public TabulatedFunctionPayload getLeft() {
        return left;
    }

    public void setLeft(TabulatedFunctionPayload left) {
        this.left = left;
    }

    public TabulatedFunctionPayload getRight() {
        return right;
    }

    public void setRight(TabulatedFunctionPayload right) {
        this.right = right;
    }
}
