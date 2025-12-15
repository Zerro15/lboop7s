package com.example.lab5.ui;

import com.example.lab5.framework.dto.DifferentiationRequest;
import com.example.lab5.framework.dto.DifferentiationResponse;
import com.example.lab5.framework.dto.TabulatedFunctionPayload;
import com.example.lab5.framework.service.TabulatedFunctionFactoryHolder;
import com.example.lab5.functions.TabulatedDifferentialOperator;
import com.example.lab5.functions.TabulatedFunction;
import com.example.lab5.functions.TabulatedFunctionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/api/tabulated-functions/differential")
public class TabulatedFunctionDifferentiationUiApiController {

    private final TabulatedFunctionFactoryHolder factoryHolder;

    public TabulatedFunctionDifferentiationUiApiController(TabulatedFunctionFactoryHolder factoryHolder) {
        this.factoryHolder = factoryHolder;
    }

    @PostMapping
    public ResponseEntity<DifferentiationResponse> differentiate(@RequestBody DifferentiationRequest request) {
        TabulatedFunctionFactory factory = factoryHolder.resolveFactory(request.getFactoryType());
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        TabulatedFunction source = toFunction(request.getFunction());
        TabulatedFunction derivative = operator.derive(source);

        TabulatedFunctionPayload payload = new TabulatedFunctionPayload();
        payload.setName("Производная");
        payload.setXValues(derivative.getXValues());
        payload.setYValues(derivative.getYValues());

        return ResponseEntity.ok(new DifferentiationResponse(payload));
    }

    private TabulatedFunction toFunction(TabulatedFunctionPayload payload) {
        if (payload == null || payload.getXValues() == null || payload.getYValues() == null) {
            throw new IllegalArgumentException("Исходная функция должна быть полностью задана");
        }
        return new TabulatedFunction(payload.getXValues(), payload.getYValues());
    }
}
