package com.example.lab5.ui;

import com.example.lab5.framework.dto.FunctionDTO;
import com.example.lab5.framework.entity.Function;

public class UiFunctionMapper {
    public FunctionDTO toDto(Function function) {
        return toDto(function, null, null);
    }

    public FunctionDTO toDto(Function function, double[] xValues, double[] yValues) {
        FunctionDTO dto = new FunctionDTO();
        dto.setId(function.getId());
        dto.setName(function.getName());
        dto.setSignature(function.getSignature());
        dto.setUserId(function.getUser().getId());
        dto.setFactoryType(function.getFactoryType());
        dto.setMathFunctionKey(function.getMathFunctionKey());
        dto.setCreationMethod(function.getCreationMethod());
        dto.setLeftBound(function.getLeftBound());
        dto.setRightBound(function.getRightBound());
        dto.setPointsCount(function.getPointsCount());

        if (xValues != null && yValues != null && xValues.length == yValues.length) {
            java.util.List<com.example.lab5.framework.dto.TabulatedPointDTO> points = new java.util.ArrayList<>();
            for (int i = 0; i < xValues.length; i++) {
                points.add(new com.example.lab5.framework.dto.TabulatedPointDTO(xValues[i], yValues[i]));
            }
            dto.setPoints(points);
        }

        return dto;
    }
}
