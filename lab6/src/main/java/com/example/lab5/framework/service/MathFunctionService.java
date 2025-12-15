package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.MathFunctionDTO;
import com.example.lab5.framework.dto.MathFunctionGroupsResponse;
import com.example.lab5.framework.dto.PreviewResponse;
import com.example.lab5.framework.service.FunctionScannerService.FunctionDescriptor;
import com.example.lab5.functions.CompositeFunction;
import com.example.lab5.functions.MathFunction;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MathFunctionService {

    private final Map<String, MathFunction> baseFunctions;
    private final List<FunctionDescriptor> descriptors;
    private final Map<String, CompositeDescriptor> customFunctions = new LinkedHashMap<>();

    public MathFunctionService(FunctionScannerService scannerService) {
        this.descriptors = Collections.unmodifiableList(scannerService.getDescriptors());
        this.baseFunctions = Collections.unmodifiableMap(descriptors.stream()
                .collect(Collectors.toMap(FunctionDescriptor::label, FunctionDescriptor::function,
                        (a, b) -> a, LinkedHashMap::new)));
    }

    public List<MathFunctionDTO> getAllMathFunctions() {
        List<MathFunctionDTO> result = new ArrayList<>();
        result.addAll(getBaseFunctionDTOs());
        result.addAll(getCustomFunctionDTOs());
        return result;
    }

    public Map<String, MathFunctionDTO> getFunctionMap() {
        Map<String, MathFunctionDTO> map = new LinkedHashMap<>();
        getAllMathFunctions().forEach(func -> map.put(func.getKey(), func));
        return map;
    }

    public MathFunction getFunctionByKey(String key) {
        if (customFunctions.containsKey(key)) {
            return customFunctions.get(key).function();
        }
        return baseFunctions.get(key);
    }

    public MathFunctionDTO createComposite(String name, String outerKey, String innerKey) {
        String trimmedName = name == null ? "" : name.trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Введите название композитной функции.");
        }
        if (baseFunctions.containsKey(trimmedName) || customFunctions.containsKey(trimmedName)) {
            throw new IllegalArgumentException("Функция с таким названием уже существует.");
        }
        MathFunction outer = requireFunction(outerKey);
        MathFunction inner = requireFunction(innerKey);

        String outerLabel = resolveLabel(outerKey);
        String innerLabel = resolveLabel(innerKey);
        String formula = String.format("%s(%s(x))", outerLabel, innerLabel);

        CompositeFunction compositeFunction = new CompositeFunction(outer, inner);
        CompositeDescriptor descriptor = new CompositeDescriptor(trimmedName, compositeFunction, formula);
        customFunctions.put(trimmedName, descriptor);
        return toDto(descriptor.name(), descriptor.name(),
                "Пользовательская композиция", formula, "Composite", true);
    }

    public MathFunctionDTO renameComposite(String oldName, String newName) {
        if (!customFunctions.containsKey(oldName)) {
            throw new IllegalArgumentException("Композитная функция не найдена: " + oldName);
        }
        String trimmed = newName == null ? "" : newName.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Введите новое имя композитной функции.");
        }
        if (baseFunctions.containsKey(trimmed) || (customFunctions.containsKey(trimmed) && !oldName.equals(trimmed))) {
            throw new IllegalArgumentException("Функция с таким названием уже существует.");
        }
        CompositeDescriptor descriptor = customFunctions.remove(oldName);
        CompositeDescriptor renamed = new CompositeDescriptor(trimmed, descriptor.function(), descriptor.formula());
        customFunctions.put(trimmed, renamed);
        return toDto(renamed.name(), renamed.name(),
                "Пользовательская композиция", renamed.formula(), "Composite", true);
    }

    public void deleteComposite(String name) {
        if (!customFunctions.containsKey(name)) {
            throw new IllegalArgumentException("Композитная функция не найдена: " + name);
        }
        customFunctions.remove(name);
    }

    public List<MathFunctionDTO> getBaseFunctionDTOs() {
        List<MathFunctionDTO> result = new ArrayList<>();
        descriptors.forEach(descriptor -> result.add(toDto(descriptor.label(), descriptor.label(),
                describe(descriptor.label()), example(descriptor.label()), category(descriptor.label()),
                descriptor.functionType(), false)));
        return result;
    }

    public List<MathFunctionDTO> getCustomFunctionDTOs() {
        return customFunctions.values().stream()
                .sorted(Comparator.comparing(CompositeDescriptor::name))
                .map(desc -> toDto(desc.name(), desc.name(), "Пользовательская композиция", desc.formula(),
                        "Composite", true))
                .collect(Collectors.toList());
    }

    public MathFunctionGroupsResponse describeGroups() {
        MathFunctionGroupsResponse response = new MathFunctionGroupsResponse();
        response.setBaseFunctions(getBaseFunctionDTOs());
        response.setCustomFunctions(getCustomFunctionDTOs());
        return response;
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

    private MathFunctionDTO toDto(String key, String label, String description,
                                  String example, String category, String functionType, boolean custom) {
        MathFunctionDTO dto = new MathFunctionDTO();
        dto.setKey(key);
        dto.setLabel(label);
        dto.setDescription(description);
        dto.setExample(example);
        dto.setCategory(category);
        dto.setFunctionType(functionType);
        dto.setCustom(custom);
        dto.setFormula(custom ? example : null);
        return dto;
    }

    private MathFunction requireFunction(String key) {
        MathFunction function = customFunctions.containsKey(key)
                ? customFunctions.get(key).function()
                : baseFunctions.get(key);
        if (function == null) {
            throw new IllegalArgumentException("Функция '" + key + "' недоступна");
        }
        return function;
    }

    private String resolveLabel(String key) {
        return getAllMathFunctions().stream()
                .filter(dto -> dto.getKey().equals(key))
                .map(MathFunctionDTO::getLabel)
                .findFirst()
                .orElse(key);
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

    private record CompositeDescriptor(String name, MathFunction function, String formula) {
    }
}
