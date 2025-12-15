package com.example.lab5.framework.dto;

import java.util.List;

public class MathFunctionGroupsResponse {
    private List<MathFunctionDTO> baseFunctions;
    private List<MathFunctionDTO> customFunctions;

    // Конструкторы
    public MathFunctionGroupsResponse() {}

    public MathFunctionGroupsResponse(List<MathFunctionDTO> baseFunctions, List<MathFunctionDTO> customFunctions) {
        this.baseFunctions = baseFunctions;
        this.customFunctions = customFunctions;
    }

    // Геттеры и сеттеры
    public List<MathFunctionDTO> getBaseFunctions() {
        return baseFunctions;
    }

    public void setBaseFunctions(List<MathFunctionDTO> baseFunctions) {
        this.baseFunctions = baseFunctions;
    }

    public List<MathFunctionDTO> getCustomFunctions() {
        return customFunctions;
    }

    public void setCustomFunctions(List<MathFunctionDTO> customFunctions) {
        this.customFunctions = customFunctions;
    }
}