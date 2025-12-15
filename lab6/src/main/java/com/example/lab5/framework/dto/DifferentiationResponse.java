package com.example.lab5.framework.dto;

import java.util.List;

public class DifferentiationResponse {
    private List<PointDTO> points;

    public List<PointDTO> getPoints() {
        return points;
    }

    public void setPoints(List<PointDTO> points) {
        this.points = points;
    }
}
