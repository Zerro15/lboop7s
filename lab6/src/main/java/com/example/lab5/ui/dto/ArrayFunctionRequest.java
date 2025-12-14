package com.example.lab5.ui.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ArrayFunctionRequest {

    @NotBlank(message = "Название функции обязательно")
    private String name;

    @NotNull(message = "Требуется идентификатор пользователя")
    private Long userId;

    private String factoryType;

    @NotEmpty(message = "Нужно задать хотя бы две точки")
    @Valid
    private List<PointDto> points;

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

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }

    public List<PointDto> getPoints() {
        return points;
    }

    public void setPoints(List<PointDto> points) {
        this.points = points;
    }
}
