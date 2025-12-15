package com.example.lab5.framework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IntegrationResponse {
    private double result;
    private String factoryType;
}
