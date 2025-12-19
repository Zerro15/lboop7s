package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class RemovePointRequest {
    private TabulatedFunctionPayload function;
    private Integer index;
    private String factoryType;
}
