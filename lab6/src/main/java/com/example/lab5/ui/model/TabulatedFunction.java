package com.example.lab5.ui.model;

import com.example.lab5.ui.dto.PointDto;

import java.util.Collections;
import java.util.List;

public class TabulatedFunction {
    private final List<PointDto> points;
    private final String sourceLabel;

    public TabulatedFunction(List<PointDto> points, String sourceLabel) {
        this.points = points;
        this.sourceLabel = sourceLabel;
    }

    public List<PointDto> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public String getSourceLabel() {
        return sourceLabel;
    }
}
