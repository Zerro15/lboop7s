package com.example.lab5.framework.dto;

import lombok.Getter;

@Getter
public class ActivateFactoryRequest {
    private String key;

    public void setKey(String key) {
        this.key = key;
    }
}
