package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.MathFunctionDTO;
import com.example.lab5.framework.dto.PreviewResponse;
import com.example.lab5.framework.service.FunctionScannerService.FunctionDescriptor;
import com.example.lab5.functions.MathFunction;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MathFunctionService {

    private final Map<String, MathFunction> availableFunctions;
    private final List<FunctionDescriptor> descriptors;

    public MathFunctionService(FunctionScannerService scannerService) {
        this.descriptors = Collections.unmodifiableList(scannerService.getDescriptors());
        this.availableFunctions = Collections.unmodifiableMap(descriptors.stream()
                .collect(Collectors.toMap(FunctionDescriptor::label, FunctionDescriptor::function,
                        (a, b) -> a, LinkedHashMap::new)));
    }

    public List<MathFunctionDTO> getAllMathFunctions() {
        List<MathFunctionDTO> result = new ArrayList<>();
        descriptors.forEach(descriptor -> result.add(createFunctionDTO(descriptor.label(), descriptor.label(),
                describe(descriptor.label()), example(descriptor.label()), category(descriptor.label()),
                descriptor.functionType())));
        return result;
    }

    public Map<String, MathFunctionDTO> getFunctionMap() {
        Map<String, MathFunctionDTO> map = new LinkedHashMap<>();
        getAllMathFunctions().forEach(func -> map.put(func.getKey(), func));
        return map;
    }

    public MathFunction getFunctionByKey(String key) {
        return availableFunctions.get(key);
    }

    public PreviewResponse previewMathFunction(String functionKey, Integer pointsCount,
                                               Double leftBound, Double rightBound) {
        MathFunction function = requireFunction(functionKey);
        validateBounds(pointsCount, leftBound, rightBound);

        PreviewResponse response = new PreviewResponse();
        List<PreviewResponse.PointData> points = new ArrayList<>();
        double step = (rightBound - leftBound) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftBound + i * step;
            double y = function.apply(x);

            PreviewResponse.PointData point = new PreviewResponse.PointData();
            point.setX(x);
            point.setY(y);
            points.add(point);
        }

        response.setPoints(points);
        return response;
    }

    private MathFunctionDTO createFunctionDTO(String key, String label, String description,
                                              String example, String category, String functionType) {
        MathFunctionDTO dto = new MathFunctionDTO();
        dto.setKey(key);
        dto.setLabel(label);
        dto.setDescription(description);
        dto.setExample(example);
        dto.setCategory(category);
        dto.setFunctionType(functionType);
        return dto;
    }

    private MathFunction requireFunction(String key) {
        MathFunction function = availableFunctions.get(key);
        if (function == null) {
            throw new IllegalArgumentException("Функция '" + key + "' недоступна");
        }
        return function;
    }

    private void validateBounds(Integer pointsCount, Double leftBound, Double rightBound) {
        if (pointsCount == null || pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftBound == null || rightBound == null || leftBound >= rightBound) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
    }

    private String describe(String label) {
        switch (label) {
            case "Квадратичная функция":
                return "f(x) = x²";
            case "Тождественная функция":
                return "f(x) = x";
            case "Синусоида":
                return "f(x) = sin(x)";
            case "Косинус":
                return "f(x) = cos(x)";
            case "Экспонента":
                return "f(x) = e^x";
            case "Нулевая функция":
                return "f(x) = 0";
            case "Натуральный логарифм":
                return "f(x) = ln(x)";
            default:
                return label;
        }
    }

    private String example(String label) {
        switch (label) {
            case "Квадратичная функция":
                return "f(x) = x * x";
            case "Тождественная функция":
                return "f(x) = x";
            case "Синусоида":
                return "f(x) = sin(x)";
            case "Косинус":
                return "f(x) = cos(x)";
            case "Экспонента":
                return "f(x) = e^x";
            case "Нулевая функция":
                return "f(x) = 0";
            case "Натуральный логарифм":
                return "f(x) = ln(x), x > 0";
            default:
                return label;
        }
    }

    private String category(String label) {
        if (label.contains("синус") || label.contains("кос")) {
            return "Тригонометрические";
        }
        if (label.contains("логарифм")) {
            return "Логарифмические";
        }
        if (label.contains("Экспонента")) {
            return "Экспоненциальные";
        }
        return "Алгебраические";
    }
}
