package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.MathFunctionDTO;
import com.example.lab5.framework.dto.PreviewResponse;
import com.example.lab5.functions.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MathFunctionService {

    private final Map<String, MathFunction> availableFunctions;

    public MathFunctionService() {
        Map<String, MathFunction> functions = new TreeMap<>();
        functions.put("Квадратичная функция", new SqrFunction());
        functions.put("Тождественная функция", new IdentityFunction());
        functions.put("Синусоида", new SinFunction());
        functions.put("Косинус", new CosFunction());
        functions.put("Экспонента", new ExpFunction());
        functions.put("Нулевая функция", new ZeroFunction());
        functions.put("Натуральный логарифм", new LogFunction());
        availableFunctions = Collections.unmodifiableMap(functions);
    }

    public List<MathFunctionDTO> getAllMathFunctions() {
        List<MathFunctionDTO> result = new ArrayList<>();
        availableFunctions.forEach((label, function) ->
                result.add(createFunctionDTO(label, label, describe(label), example(label), category(label), function.getClass().getSimpleName())));
        result.sort(Comparator.comparing(MathFunctionDTO::getLabel));
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
