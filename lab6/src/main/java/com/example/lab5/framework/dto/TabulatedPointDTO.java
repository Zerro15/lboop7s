package com.example.lab5.framework.dto;

public class TabulatedPointDTO {
    private double x;
    private double y;

    // Конструкторы
    public TabulatedPointDTO() {}

    public TabulatedPointDTO(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Геттеры и сеттеры
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
}