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

    // Константы типов функций (вместо Enum)
    public static final String FUNCTION_TYPE_ALGEBRAIC = "ALGEBRAIC";
    public static final String FUNCTION_TYPE_TRIGONOMETRIC = "TRIGONOMETRIC";
    public static final String FUNCTION_TYPE_LOGARITHMIC = "LOGARITHMIC";
    public static final String FUNCTION_TYPE_EXPONENTIAL = "EXPONENTIAL";
    public static final String FUNCTION_TYPE_COMPOSITE = "COMPOSITE";
    public static final String FUNCTION_TYPE_CONSTANT = "CONSTANT";
    public static final String FUNCTION_TYPE_POLYNOMIAL = "POLYNOMIAL";
    public static final String FUNCTION_TYPE_RATIONAL = "RATIONAL";

    // Маппинг для отображения на русский язык
    private static final Map<String, String> FUNCTION_TYPE_DISPLAY_NAMES = Map.of(
            FUNCTION_TYPE_ALGEBRAIC, "Алгебраическая",
            FUNCTION_TYPE_TRIGONOMETRIC, "Тригонометрическая",
            FUNCTION_TYPE_LOGARITHMIC, "Логарифмическая",
            FUNCTION_TYPE_EXPONENTIAL, "Экспоненциальная",
            FUNCTION_TYPE_COMPOSITE, "Композитная",
            FUNCTION_TYPE_CONSTANT, "Константа",
            FUNCTION_TYPE_POLYNOMIAL, "Полиномиальная",
            FUNCTION_TYPE_RATIONAL, "Рациональная"
    );

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
                "Пользовательская композиция", formula, "Composite",
                FUNCTION_TYPE_COMPOSITE, true);
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
                "Пользовательская композиция", renamed.formula(), "Composite",
                FUNCTION_TYPE_COMPOSITE, true);
    }

    public void deleteComposite(String name) {
        if (!customFunctions.containsKey(name)) {
            throw new IllegalArgumentException("Композитная функция не найдена: " + name);
        }
        customFunctions.remove(name);
    }

    public List<MathFunctionDTO> getBaseFunctionDTOs() {
        List<MathFunctionDTO> result = new ArrayList<>();
        descriptors.forEach(descriptor -> {
            String label = descriptor.label();
            result.add(toDto(label, label,
                    describe(label), example(label), category(label),
                    getFunctionType(label), false));
        });
        return result;
    }

    public List<MathFunctionDTO> getCustomFunctionDTOs() {
        return customFunctions.values().stream()
                .sorted(Comparator.comparing(CompositeDescriptor::name))
                .map(desc -> toDto(desc.name(), desc.name(), "Пользовательская композиция", desc.formula(),
                        "Composite", FUNCTION_TYPE_COMPOSITE, true))
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
            point.setX(Double.valueOf(x));
            point.setY(Double.valueOf(y));
            points.add(point);
        }

        response.setPoints(points);
        return response;
    }

    // ДОБАВЛЕННЫЙ МЕТОД: Определение типа функции по её названию
    public String getFunctionType(String functionLabel) {
        if (functionLabel == null) {
            return FUNCTION_TYPE_ALGEBRAIC;
        }

        String label = functionLabel.toLowerCase();

        if (label.contains("синус") || label.contains("косин") || label.contains("sin") || label.contains("cos") ||
                label.contains("тан") || label.contains("cot") || label.contains("тригон")) {
            return FUNCTION_TYPE_TRIGONOMETRIC;
        }

        if (label.contains("лог") || label.contains("ln") || label.contains("log") || label.contains("логарифм")) {
            return FUNCTION_TYPE_LOGARITHMIC;
        }

        if (label.contains("экспонент") || label.contains("exp") || label.contains("e^")) {
            return FUNCTION_TYPE_EXPONENTIAL;
        }

        if (label.contains("композит") || label.contains("составн") || label.contains("сложн")) {
            return FUNCTION_TYPE_COMPOSITE;
        }

        if (label.contains("нулев") || label.contains("констант") || label.contains("постоян")) {
            return FUNCTION_TYPE_CONSTANT;
        }

        if (label.contains("квадрат") || label.contains("степен") || label.contains("полином") ||
                label.contains("x²") || label.contains("x^")) {
            return FUNCTION_TYPE_POLYNOMIAL;
        }

        if (label.contains("тождеств") || label.contains("идентичн") || label.equals("x")) {
            return FUNCTION_TYPE_ALGEBRAIC;
        }

        return FUNCTION_TYPE_ALGEBRAIC;
    }

    // ДОБАВЛЕННЫЙ МЕТОД: Получение отображаемого имени типа функции
    public String getFunctionTypeDisplayName(String functionType) {
        return FUNCTION_TYPE_DISPLAY_NAMES.getOrDefault(functionType, functionType);
    }

    // ДОБАВЛЕННЫЙ МЕТОД: Получение всех функций определённого типа
    public List<MathFunctionDTO> getFunctionsByType(String type) {
        return getAllMathFunctions().stream()
                .filter(dto -> getFunctionType(dto.getLabel()).equals(type))
                .collect(Collectors.toList());
    }

    // ДОБАВЛЕННЫЙ МЕТОД: Получение статистики по типам функций
    public Map<String, Long> getFunctionTypeStatistics() {
        return getAllMathFunctions().stream()
                .collect(Collectors.groupingBy(
                        dto -> getFunctionType(dto.getLabel()),
                        Collectors.counting()
                ));
    }

    // ДОБАВЛЕННЫЙ МЕТОД: Получение всех доступных типов функций
    public List<String> getAvailableFunctionTypes() {
        return List.of(
                FUNCTION_TYPE_ALGEBRAIC,
                FUNCTION_TYPE_TRIGONOMETRIC,
                FUNCTION_TYPE_LOGARITHMIC,
                FUNCTION_TYPE_EXPONENTIAL,
                FUNCTION_TYPE_COMPOSITE,
                FUNCTION_TYPE_CONSTANT,
                FUNCTION_TYPE_POLYNOMIAL,
                FUNCTION_TYPE_RATIONAL
        );
    }

    // ДОБАВЛЕННЫЙ МЕТОД: Получение всех типов функций с отображаемыми именами
    public Map<String, String> getFunctionTypesWithDisplayNames() {
        Map<String, String> result = new LinkedHashMap<>();
        for (String type : getAvailableFunctionTypes()) {
            result.put(type, getFunctionTypeDisplayName(type));
        }
        return result;
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
                return "f(x) = ln(x), x > 0";
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