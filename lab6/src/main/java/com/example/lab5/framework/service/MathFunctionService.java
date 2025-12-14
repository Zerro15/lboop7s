package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.*;
import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;
import com.example.lab5.framework.math.functions.AnnotatedMathFunction;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MathFunctionService {

    public List<MathFunctionDTO> getAllMathFunctions() {
        return loadAnnotatedFunctions().stream()
                .map(this::toDto)
                .sorted(Comparator
                        .comparingInt(MathFunctionDTO::getPriority)
                        .thenComparing(MathFunctionDTO::getLabel))
                .collect(Collectors.toList());
    }

    public Map<String, MathFunctionDTO> getFunctionMap() {
        return getAllMathFunctions().stream()
                .collect(Collectors.toMap(MathFunctionDTO::getKey, f -> f));
    }

    public PreviewResponse previewMathFunction(String functionKey, Integer pointsCount,
                                               Double leftBound, Double rightBound) {
        PreviewResponse response = new PreviewResponse();
        List<PreviewResponse.PointData> points = new ArrayList<>();

        double step = (rightBound - leftBound) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftBound + i * step;
            double y = calculateFunction(functionKey, x);

            PreviewResponse.PointData point = new PreviewResponse.PointData();
            point.setX(x);
            point.setY(y);
            points.add(point);
        }

        response.setPoints(points);
        return response;
    }

    public double calculateFunction(String functionKey, double x) {
        return loadAnnotatedFunctions().stream()
                .filter(f -> f.getDescriptor().key().equals(functionKey))
                .findFirst()
                .map(func -> func.getFunction().apply(x))
                .orElse(Double.NaN);
    }

    private List<AnnotatedMathFunction> loadAnnotatedFunctions() {
        return AnnotatedMathFunction.discoveryCache();
    }

    private MathFunctionDTO toDto(AnnotatedMathFunction annotated) {
        MathFunctionDescriptor meta = annotated.getDescriptor();
        MathFunctionDTO dto = new MathFunctionDTO();
        dto.setKey(meta.key());
        dto.setLabel(meta.localizedName());
        dto.setDescription(meta.description());
        dto.setExample(meta.example());
        dto.setCategory(meta.category());
        dto.setFunctionType(annotated.getFunction().getClass().getSimpleName());
        dto.setPriority(meta.priority());
        return dto;
    }
}