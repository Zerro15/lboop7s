package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class IntegrationRequest {
    private TabulatedFunctionPayload function;
    private Integer threads;
    private String factoryType;
}
