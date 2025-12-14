package com.example.lab5.ui.service;

import com.example.lab5.framework.dto.CreateFromArraysRequest;
import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.service.FunctionService;
import com.example.lab5.ui.dto.ArrayFunctionRequest;
import com.example.lab5.ui.dto.MathFunctionRequest;
import com.example.lab5.ui.dto.PointDto;
import com.example.lab5.ui.dto.UiFunctionResponse;
import com.example.lab5.ui.factory.TabulatedFunctionFactory;
import com.example.lab5.ui.math.MathFunction;
import com.example.lab5.ui.model.TabulatedFunction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UiFunctionService {

    private final TabulatedFunctionFactory tabulatedFunctionFactory;
    private final Map<String, MathFunction> localizedFunctionMap;
    private final FunctionService functionService;

    public UiFunctionService(TabulatedFunctionFactory tabulatedFunctionFactory,
                             Map<String, MathFunction> localizedFunctionMap,
                             FunctionService functionService) {
        this.tabulatedFunctionFactory = tabulatedFunctionFactory;
        this.localizedFunctionMap = localizedFunctionMap;
        this.functionService = functionService;
    }

    public UiFunctionResponse createFromArrays(ArrayFunctionRequest request) {
        TabulatedFunction tabulatedFunction = tabulatedFunctionFactory.createFromArrays(request.getPoints());

        List<CreateFromArraysRequest.PointData> points = tabulatedFunction.getPoints().stream()
                .map(point -> {
                    CreateFromArraysRequest.PointData dto = new CreateFromArraysRequest.PointData();
                    dto.setX(point.getX());
                    dto.setY(point.getY());
                    return dto;
                })
                .collect(Collectors.toList());

        Function created = functionService.createFromArrays(
                request.getUserId(),
                request.getName(),
                points,
                request.getFactoryType()
        );

        return new UiFunctionResponse(
                created.getId(),
                created.getName(),
                "arrays",
                created.getFactoryType(),
                tabulatedFunction.getPoints()
        );
    }

    public UiFunctionResponse createFromMathFunction(MathFunctionRequest request) {
        MathFunction mathFunction = localizedFunctionMap.get(request.getLocalizedName());
        if (mathFunction == null) {
            throw new IllegalArgumentException("Выбранная математическая функция не поддерживается");
        }

        TabulatedFunction tabulatedFunction = tabulatedFunctionFactory.createFromMathFunction(
                mathFunction,
                request.getLeftBound(),
                request.getRightBound(),
                request.getPointsCount()
        );

        Function created = functionService.createFromMathFunction(
                request.getUserId(),
                request.getName(),
                mathFunction.getKey(),
                request.getPointsCount(),
                request.getLeftBound(),
                request.getRightBound(),
                request.getFactoryType()
        );

        return new UiFunctionResponse(
                created.getId(),
                created.getName(),
                "math_function",
                created.getFactoryType(),
                tabulatedFunction.getPoints()
        );
    }
}
