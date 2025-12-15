package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class TabulatedFunctionPayload {
    private String name;
    private double[] xValues;
    private double[] yValues;
    private String factoryType;
    private boolean insertable;
    private boolean removable;
}
