package com.example.lab5.ui;

import com.example.lab5.framework.dto.FunctionDTO;
import com.example.lab5.framework.entity.Function;

public class UiFunctionMapper {
    public FunctionDTO toDto(Function function) {
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
        return dto;
    }
}
