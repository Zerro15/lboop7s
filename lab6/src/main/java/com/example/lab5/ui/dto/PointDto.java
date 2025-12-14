package com.example.lab5.ui.dto;

import javax.validation.constraints.NotNull;

public class PointDto {
    @NotNull(message = "Значение X обязательно")
    private Double x;

    @NotNull(message = "Значение Y обязательно")
    private Double y;

    public PointDto() {
    }

    public PointDto(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
