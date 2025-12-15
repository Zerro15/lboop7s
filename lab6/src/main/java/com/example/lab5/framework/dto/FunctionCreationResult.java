package com.example.lab5.framework.dto;

import com.example.lab5.framework.entity.Function;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FunctionCreationResult {
    private Function function;
    private double[] xValues;
    private double[] yValues;
}
