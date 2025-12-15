package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.MathFunctionDTO;
import com.example.lab5.framework.dto.PreviewResponse;
import com.example.lab5.functions.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.util.*;

@Service
public class MathFunctionService {

    private final Map<String, MathFunction> availableFunctions;

    public MathFunctionService() {
        Map<String, MathFunction> functions = new TreeMap<>();
        discoverMathFunctions(functions);
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

    private void discoverMathFunctions(Map<String, MathFunction> functions) {
        Map<String, MathFunction> defaults = new LinkedHashMap<>();
        defaults.put("Квадратичная функция", new SqrFunction());
        defaults.put("Тождественная функция", new IdentityFunction());
        defaults.put("Синусоида", new SinFunction());
        defaults.put("Косинус", new CosFunction());
        defaults.put("Экспонента", new ExpFunction());
        defaults.put("Нулевая функция", new ZeroFunction());
        defaults.put("Натуральный логарифм", new LogFunction());

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(MathFunction.class));

        Set<String> usedLabels = new HashSet<>();
        for (BeanDefinition bean : scanner.findCandidateComponents("com.example.lab5.functions")) {
            try {
                Class<?> clazz = Class.forName(bean.getBeanClassName());
                if (!MathFunction.class.isAssignableFrom(clazz) || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }
                MathFunction instance = (MathFunction) clazz.getDeclaredConstructor().newInstance();
                String label = resolveLabel(clazz.getSimpleName());
                if (usedLabels.add(label)) {
                    functions.put(label, instance);
                }
            } catch (Exception ignored) {
                // игнорируем нестабильные классы
            }
        }

        defaults.forEach((label, fn) -> functions.putIfAbsent(label, fn));
    }

    private String resolveLabel(String simpleName) {
        Map<String, String> overrides = new HashMap<>();
        overrides.put("SqrFunction", "Квадратичная функция");
        overrides.put("IdentityFunction", "Тождественная функция");
        overrides.put("SinFunction", "Синусоида");
        overrides.put("CosFunction", "Косинус");
        overrides.put("ExpFunction", "Экспонента");
        overrides.put("ZeroFunction", "Нулевая функция");
        overrides.put("LogFunction", "Натуральный логарифм");
        return overrides.getOrDefault(simpleName, simpleName.replace("Function", "").replaceAll("([A-Z])", " $1").trim());
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
