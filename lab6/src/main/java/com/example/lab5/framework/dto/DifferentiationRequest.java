package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class DifferentiationRequest {
    private String factoryType;
    private TabulatedFunctionPayload function;
}
