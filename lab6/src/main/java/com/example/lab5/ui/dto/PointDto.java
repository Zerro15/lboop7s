package com.example.lab5.ui.dto;

import jakarta.validation.constraints.NotNull;

public class PointDto {
    @NotNull(message = "X должен быть числом")
    private Double x;

    @NotNull(message = "Y должен быть числом")
    private Double y;

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
