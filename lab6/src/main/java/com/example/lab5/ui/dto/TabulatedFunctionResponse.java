package com.example.lab5.ui.dto;

import java.util.List;

public class TabulatedFunctionResponse {
    private List<PointDto> points;

    public TabulatedFunctionResponse() {
    }

    public TabulatedFunctionResponse(List<PointDto> points) {
        this.points = points;
    }

    public List<PointDto> getPoints() {
        return points;
    }

    public void setPoints(List<PointDto> points) {
        this.points = points;
    }
}
