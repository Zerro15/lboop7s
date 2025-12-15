package com.example.lab5.ui;

import com.example.lab5.framework.dto.CreateFromArraysRequest;
import com.example.lab5.framework.dto.CreateFromMathRequest;
import com.example.lab5.framework.dto.EvaluateTabulatedRequest;
import com.example.lab5.framework.dto.FunctionCreationResult;
import com.example.lab5.framework.dto.FunctionDTO;
import com.example.lab5.framework.dto.InsertPointRequest;
import com.example.lab5.framework.dto.RemovePointRequest;
import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.service.FunctionService;
import com.example.lab5.framework.service.TabulatedFunctionMutationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/api/tabulated-functions")
public class TabulatedFunctionUiApiController {

    private final FunctionService functionService;
    private final TabulatedFunctionMutationService mutationService;
    private final UiFunctionMapper mapper = new UiFunctionMapper();

    public TabulatedFunctionUiApiController(FunctionService functionService,
                                            TabulatedFunctionMutationService mutationService) {
        this.functionService = functionService;
        this.mutationService = mutationService;
    }

    @PostMapping("/arrays")
    public ResponseEntity<FunctionDTO> createFromArrays(@RequestBody CreateFromArraysRequest request) {
        FunctionCreationResult result = functionService.createFromArrays(
                request.getUserId(),
                request.getName(),
                request.getPoints(),
                request.getFactoryType()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toDto(result.getFunction(), result.getXValues(), result.getYValues()));
    }

    @PostMapping("/math-function")
    public ResponseEntity<FunctionDTO> createFromMathFunction(@RequestBody CreateFromMathRequest request) {
        FunctionCreationResult result = functionService.createFromMathFunction(
                request.getUserId(),
                request.getName(),
                request.getMathFunctionKey(),
                request.getPointsCount(),
                request.getLeftBound(),
                request.getRightBound(),
                request.getFactoryType()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toDto(result.getFunction(), result.getXValues(), result.getYValues()));
    }

    @PostMapping("/apply")
    public ResponseEntity<?> evaluateTabulated(@RequestBody EvaluateTabulatedRequest request) {
        return ResponseEntity.ok(functionService.evaluateTabulated(request));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertPoint(@RequestBody InsertPointRequest request) {
        return ResponseEntity.ok(
                mutationService.insert(request.getFunction(), request.getX(), request.getY(), request.getFactoryType())
        );
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removePoint(@RequestBody RemovePointRequest request) {
        return ResponseEntity.ok(
                mutationService.remove(request.getFunction(), request.getIndex(), request.getFactoryType())
        );
    }
}
