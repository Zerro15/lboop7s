package com.example.lab5.framework.dto;

import java.util.List;

public class PreviewResponse {
    private List<PointData> points;

    // Конструкторы
    public PreviewResponse() {}

    public PreviewResponse(List<PointData> points) {
        this.points = points;
    }

    // Геттеры и сеттеры
    public List<PointData> getPoints() {
        return points;
    }

    public void setPoints(List<PointData> points) {
        this.points = points;
    }

    // Внутренний класс PointData
    public static class PointData {
        private Double x;
        private Double y;

        // Конструкторы
        public PointData() {}

        public PointData(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        // Геттеры и сеттеры
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
}