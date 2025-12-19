package com.example.lab5.framework.dto;

public class PreviewRequest {
    private String mathFunctionKey;
    private Integer pointsCount;
    private Double leftBound;
    private Double rightBound;

    // Конструкторы
    public PreviewRequest() {}

    public PreviewRequest(String mathFunctionKey, Integer pointsCount, Double leftBound, Double rightBound) {
        this.mathFunctionKey = mathFunctionKey;
        this.pointsCount = pointsCount;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    // Геттеры и сеттеры
    public String getMathFunctionKey() {
        return mathFunctionKey;
    }

    public void setMathFunctionKey(String mathFunctionKey) {
        this.mathFunctionKey = mathFunctionKey;
    }

    public Integer getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(Integer pointsCount) {
        this.pointsCount = pointsCount;
    }

    public Double getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(Double leftBound) {
        this.leftBound = leftBound;
    }

    public Double getRightBound() {
        return rightBound;
    }

    public void setRightBound(Double rightBound) {
        this.rightBound = rightBound;
    }
}