package com.example.lab5.framework.dto;

import java.util.List;

public class PreviewResponse {
    private List<PointData> points;

    // Геттеры и сеттеры
    public List<PointData> getPoints() { return points; }
    public void setPoints(List<PointData> points) { this.points = points; }

    public static class PointData {
        private Double x;
        private Double y;

        // Геттеры и сеттеры
        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }

        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }
    }
}