package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class OperationRequest {
    private String operation; // add, subtract, multiply, divide
    private String factoryType;
    private TabulatedFunctionPayload left;
    private TabulatedFunctionPayload right;
}
