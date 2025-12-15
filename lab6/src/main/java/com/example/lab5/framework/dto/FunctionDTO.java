package com.example.lab5.framework.dto;

import java.util.List;

public class FunctionDTO {
    private Long id;
    private String name;
    private String signature;
    private Long userId;

    // Поля для Lab 7
    private String factoryType;
    private String mathFunctionKey;
    private String creationMethod;
    private Double leftBound;
    private Double rightBound;
    private Integer pointsCount;
    private List<TabulatedPointDTO> points;
    private boolean insertable;
    private boolean removable;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFactoryType() { return factoryType; }
    public void setFactoryType(String factoryType) { this.factoryType = factoryType; }

    public String getMathFunctionKey() { return mathFunctionKey; }
    public void setMathFunctionKey(String mathFunctionKey) { this.mathFunctionKey = mathFunctionKey; }

    public String getCreationMethod() { return creationMethod; }
    public void setCreationMethod(String creationMethod) { this.creationMethod = creationMethod; }

    public Double getLeftBound() { return leftBound; }
    public void setLeftBound(Double leftBound) { this.leftBound = leftBound; }

    public Double getRightBound() { return rightBound; }
    public void setRightBound(Double rightBound) { this.rightBound = rightBound; }

    public Integer getPointsCount() { return pointsCount; }
    public void setPointsCount(Integer pointsCount) { this.pointsCount = pointsCount; }

    public List<TabulatedPointDTO> getPoints() { return points; }
    public void setPoints(List<TabulatedPointDTO> points) { this.points = points; }

    public boolean isInsertable() { return insertable; }
    public void setInsertable(boolean insertable) { this.insertable = insertable; }

    public boolean isRemovable() { return removable; }
    public void setRemovable(boolean removable) { this.removable = removable; }
}