package com.example.lab5.framework.dto;

public class CompositeCreateRequest {
    private String name;
    private String outerKey;
    private String innerKey;

    // Конструкторы
    public CompositeCreateRequest() {}

    public CompositeCreateRequest(String name, String outerKey, String innerKey) {
        this.name = name;
        this.outerKey = outerKey;
        this.innerKey = innerKey;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOuterKey() {
        return outerKey;
    }

    public void setOuterKey(String outerKey) {
        this.outerKey = outerKey;
    }

    public String getInnerKey() {
        return innerKey;
    }

    public void setInnerKey(String innerKey) {
        this.innerKey = innerKey;
    }
}
