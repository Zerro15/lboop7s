package com.example.lab5.framework.dto;

import lombok.Data;

import java.util.List;

@Data
public class TabulatedFunctionResponse {
    private Long id;
    private String name;
    private String factoryType;
    private List<TabulatedPointDTO> points;
}
