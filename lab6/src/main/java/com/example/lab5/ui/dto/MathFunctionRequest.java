package com.example.lab5.ui.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MathFunctionRequest {

    @NotBlank(message = "Название функции обязательно")
    private String localizedName;

    @NotNull(message = "Количество точек обязательно")
    @Min(value = 2, message = "Количество точек должно быть не менее 2")
    private Integer pointsCount;

    @NotNull(message = "Левая граница обязательна")
    private Double leftBound;

    @NotNull(message = "Правая граница обязательна")
    private Double rightBound;

    private String factoryType;

    @NotBlank(message = "Название функции обязательно")
    private String name;

    @NotNull(message = "Требуется идентификатор пользователя")
    private Long userId;

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
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

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
