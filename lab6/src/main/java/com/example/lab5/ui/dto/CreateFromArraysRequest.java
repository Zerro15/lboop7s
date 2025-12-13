package com.example.lab5.ui.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateFromArraysRequest {
    @NotEmpty(message = "Необходимо задать хотя бы одну точку")
    @Valid
    private List<PointDto> points;

    public List<PointDto> getPoints() {
        return points;
    }

    public void setPoints(List<PointDto> points) {
        this.points = points;
    }
}
