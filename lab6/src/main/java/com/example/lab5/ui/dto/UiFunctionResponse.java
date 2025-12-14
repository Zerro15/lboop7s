package com.example.lab5.ui.dto;

import java.util.List;

public class UiFunctionResponse {
    private Long functionId;
    private String name;
    private String creationMethod;
    private String factoryType;
    private List<PointDto> points;

    public UiFunctionResponse(Long functionId, String name, String creationMethod, String factoryType, List<PointDto> points) {
        this.functionId = functionId;
        this.name = name;
        this.creationMethod = creationMethod;
        this.factoryType = factoryType;
        this.points = points;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public String getName() {
        return name;
    }

    public String getCreationMethod() {
        return creationMethod;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public List<PointDto> getPoints() {
        return points;
    }
}
