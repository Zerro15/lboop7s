package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class InsertPointRequest {
    private TabulatedFunctionPayload function;
    private Double x;
    private Double y;
    private String factoryType;
}
