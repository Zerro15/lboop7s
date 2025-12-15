package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class CompositeCreateRequest {
    private String name;
    private String outerKey;
    private String innerKey;
}
