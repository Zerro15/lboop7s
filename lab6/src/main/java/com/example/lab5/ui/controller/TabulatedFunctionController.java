package com.example.lab5.ui.controller;

import com.example.lab5.functions.TabulatedFunction;
import com.example.lab5.ui.dto.CreateFromArraysRequest;
import com.example.lab5.ui.dto.CreateFromMathFunctionRequest;
import com.example.lab5.ui.dto.PointDto;
import com.example.lab5.ui.dto.TabulatedFunctionResponse;
import com.example.lab5.ui.service.TabulatedFunctionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/functions")
public class TabulatedFunctionController {
    private final TabulatedFunctionService functionService;

    public TabulatedFunctionController(TabulatedFunctionService functionService) {
        this.functionService = functionService;
    }

    @GetMapping("/math-functions")
    public List<String> getAvailableMathFunctions() {
        return functionService.listFunctionNames();
    }

    @PostMapping("/create-from-arrays")
    @ResponseStatus(HttpStatus.CREATED)
    public TabulatedFunctionResponse createFromArrays(@Valid @RequestBody CreateFromArraysRequest request) {
        TabulatedFunction function = functionService.createFromArrays(request);
        return mapToResponse(function);
    }

    @PostMapping("/create-from-math-function")
    @ResponseStatus(HttpStatus.CREATED)
    public TabulatedFunctionResponse createFromMathFunction(@Valid @RequestBody CreateFromMathFunctionRequest request) {
        TabulatedFunction function = functionService.createFromMathFunction(request);
        return mapToResponse(function);
    }

    private TabulatedFunctionResponse mapToResponse(TabulatedFunction function) {
        List<PointDto> points = function.getPoints().stream()
                .map(p -> {
                    PointDto dto = new PointDto();
                    dto.setX(p.x());
                    dto.setY(p.y());
                    return dto;
                })
                .collect(Collectors.toList());
        return new TabulatedFunctionResponse(points);
    }
}
