package com.example.lab5.ui;

import com.example.lab5.framework.dto.OperationRequest;
import com.example.lab5.framework.dto.OperationResponse;
import com.example.lab5.framework.dto.TabulatedFunctionPayload;
import com.example.lab5.framework.service.TabulatedFunctionFactoryHolder;
import com.example.lab5.functions.Insertable;
import com.example.lab5.functions.Removable;
import com.example.lab5.functions.TabulatedFunction;
import com.example.lab5.functions.TabulatedFunctionFactory;
import com.example.lab5.functions.TabulatedFunctionOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/api/tabulated-functions/operations")
public class TabulatedFunctionOperationsUiApiController {

    private final TabulatedFunctionFactoryHolder factoryHolder;

    public TabulatedFunctionOperationsUiApiController(TabulatedFunctionFactoryHolder factoryHolder) {
        this.factoryHolder = factoryHolder;
    }

    @PostMapping
    public ResponseEntity<OperationResponse> applyOperation(@RequestBody OperationRequest request) {
        TabulatedFunctionFactory factory = factoryHolder.resolveFactory(request.getFactoryType());
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(factory);

        TabulatedFunction left = toFunction(request.getLeft());
        TabulatedFunction right = toFunction(request.getRight());

        TabulatedFunction result;
        switch (request.getOperation()) {
            case "add":
                result = service.add(left, right);
                break;
            case "subtract":
                result = service.subtract(left, right);
                break;
            case "multiply":
                result = service.multiply(left, right);
                break;
            case "divide":
                result = service.divide(left, right);
                break;
            default:
                throw new IllegalArgumentException("Неизвестная операция: " + request.getOperation());
        }

        TabulatedFunctionPayload payload = new TabulatedFunctionPayload();
        payload.setName("Результат");
        payload.setXValues(result.getXValues());
        payload.setYValues(result.getYValues());
        payload.setFactoryType(factoryHolder.resolveKey(request.getFactoryType()));
        payload.setInsertable(result instanceof Insertable);
        payload.setRemovable(result instanceof Removable);

        return ResponseEntity.ok(new OperationResponse(payload));
    }

    private TabulatedFunction toFunction(TabulatedFunctionPayload payload) {
        if (payload == null || payload.getXValues() == null || payload.getYValues() == null) {
            throw new IllegalArgumentException("Оба операнда должны быть заданы полностью");
        }
        return new TabulatedFunction(payload.getXValues(), payload.getYValues());
    }
}
