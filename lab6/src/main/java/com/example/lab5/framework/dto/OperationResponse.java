package com.example.lab5.framework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperationResponse {
    private TabulatedFunctionPayload result;
}
