package com.example.lab5.ui.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateFromMathFunctionRequest {
    @NotBlank(message = "Необходимо выбрать функцию")
    private String functionName;

    @NotNull(message = "Количество точек обязательно")
    @Min(value = 2, message = "Количество точек должно быть не меньше 2")
    private Integer pointsCount;

    @NotNull(message = "Начало интервала обязательно")
    private Double start;

    @NotNull(message = "Конец интервала обязателен")
    private Double end;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Integer getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(Integer pointsCount) {
        this.pointsCount = pointsCount;
    }

    public Double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start;
    }

    public Double getEnd() {
        return end;
    }

    public void setEnd(Double end) {
        this.end = end;
    }
}
