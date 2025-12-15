package com.example.lab5.framework.dto;

public class CompositeRenameRequest {
    private String oldName;
    private String newName;

    // Конструкторы
    public CompositeRenameRequest() {}

    public CompositeRenameRequest(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    // Геттеры и сеттеры
    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}