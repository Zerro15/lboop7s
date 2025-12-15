package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class EvaluateTabulatedRequest {
    private TabulatedFunctionPayload function;
    private Double x;
    private String factoryType;
}
