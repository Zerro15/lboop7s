package com.example.lab5.framework.dto;

import lombok.Data;

import java.util.List;

@Data
public class MathFunctionGroupsResponse {
    private List<MathFunctionDTO> baseFunctions;
    private List<MathFunctionDTO> customFunctions;
}
