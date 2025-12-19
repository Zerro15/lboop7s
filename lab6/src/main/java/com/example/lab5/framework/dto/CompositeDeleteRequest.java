package com.example.lab5.framework.dto;

public class CompositeDeleteRequest {
    private String name;

    // Конструкторы
    public CompositeDeleteRequest() {}

    public CompositeDeleteRequest(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}