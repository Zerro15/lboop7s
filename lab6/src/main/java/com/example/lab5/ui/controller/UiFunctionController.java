package com.example.lab5.ui.controller;

import com.example.lab5.ui.dto.ArrayFunctionRequest;
import com.example.lab5.ui.dto.MathFunctionOption;
import com.example.lab5.ui.dto.MathFunctionRequest;
import com.example.lab5.ui.dto.UiFunctionResponse;
import com.example.lab5.ui.service.UiFunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/functions")
@Validated
public class UiFunctionController {

    private static final Logger logger = LoggerFactory.getLogger(UiFunctionController.class);

    private final UiFunctionService uiFunctionService;
    private final List<MathFunctionOption> mathFunctionOptions;

    public UiFunctionController(UiFunctionService uiFunctionService, List<MathFunctionOption> mathFunctionOptions) {
        this.uiFunctionService = uiFunctionService;
        this.mathFunctionOptions = mathFunctionOptions;
    }

    @PostMapping("/create-from-arrays")
    public ResponseEntity<UiFunctionResponse> createFromArrays(@Valid @RequestBody ArrayFunctionRequest request) {
        logger.info("Создание табулированной функции из массива точек, точек: {}", request.getPoints().size());
        UiFunctionResponse response = uiFunctionService.createFromArrays(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-from-math-function")
    public ResponseEntity<UiFunctionResponse> createFromMath(@Valid @RequestBody MathFunctionRequest request) {
        logger.info("Создание табулированной функции из математической функции '{}', точек: {}",
                request.getLocalizedName(), request.getPointsCount());
        UiFunctionResponse response = uiFunctionService.createFromMathFunction(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/math-functions")
    public List<MathFunctionOption> getMathFunctionOptions() {
        return mathFunctionOptions;
    }
}
