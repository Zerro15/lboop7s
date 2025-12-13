package com.example.lab5.ui.service;

import com.example.lab5.functions.MathFunction;
import com.example.lab5.functions.Point;
import com.example.lab5.functions.TabulatedFunction;
import com.example.lab5.functions.TabulatedFunctionFactory;
import com.example.lab5.ui.dto.CreateFromArraysRequest;
import com.example.lab5.ui.dto.CreateFromMathFunctionRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TabulatedFunctionService {

    private final TabulatedFunctionFactory functionFactory;
    private final Map<String, MathFunction> functions;

    public TabulatedFunctionService(TabulatedFunctionFactory functionFactory, Map<String, MathFunction> functions) {
        this.functionFactory = functionFactory;
        this.functions = functions;
    }

    public TabulatedFunction createFromArrays(CreateFromArraysRequest request) {
        List<Point> points = request.getPoints().stream()
                .map(p -> new Point(p.getX(), p.getY()))
                .collect(Collectors.toList());
        return functionFactory.createFromArrays(points);
    }

    public TabulatedFunction createFromMathFunction(CreateFromMathFunctionRequest request) {
        MathFunction function = functions.get(request.getFunctionName());
        if (function == null) {
            throw new IllegalArgumentException("Выбрана неизвестная функция");
        }
        if (request.getStart() >= request.getEnd()) {
            throw new IllegalArgumentException("Начало интервала должно быть меньше конца");
        }
        return functionFactory.createFromMathFunction(
                function,
                request.getStart(),
                request.getEnd(),
                request.getPointsCount()
        );
    }

    public List<String> listFunctionNames() {
        return functions.keySet().stream().sorted(String::compareToIgnoreCase).toList();
    }
}
