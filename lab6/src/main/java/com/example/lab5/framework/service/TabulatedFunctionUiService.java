package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.*;
import com.example.lab5.functions.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TabulatedFunctionUiService {

    private final TabulatedFunctionFactoryHolder factoryHolder;
    private final TabulatedIntegrationService integrationService;
    private final TabulatedFunctionMutationService mutationService;
    private final FunctionService functionService;

    public TabulatedFunctionUiService(TabulatedFunctionFactoryHolder factoryHolder,
                                      TabulatedIntegrationService integrationService,
                                      TabulatedFunctionMutationService mutationService,
                                      FunctionService functionService) {
        this.factoryHolder = factoryHolder;
        this.integrationService = integrationService;
        this.mutationService = mutationService;
        this.functionService = functionService;
    }

    public OperationResponse operate(OperationRequest request) {
        validatePayload(request.getLeft());
        validatePayload(request.getRight());
        TabulatedFunctionFactory factory = factoryHolder.resolveFactory(request.getFactoryType());
        TabulatedFunction left = toFunction(factory, request.getLeft());
        TabulatedFunction right = toFunction(factory, request.getRight());
        TabulatedFunctionOperationService opService = new TabulatedFunctionOperationService(factory);

        TabulatedFunction result = switch (request.getOperation()) {
            case "add" -> opService.add(left, right);
            case "subtract" -> opService.subtract(left, right);
            case "multiply" -> opService.multiply(left, right);
            case "divide" -> opService.divide(left, right);
            default -> throw new IllegalArgumentException("Неизвестная операция: " + request.getOperation());
        };

        TabulatedFunctionPayload payload = toPayload(request.getLeft().getName(), result, request.getFactoryType());
        return new OperationResponse(payload);
    }

    public DifferentiationResponse differentiate(DifferentiationRequest request) {
        validatePayload(request.getFunction());
        TabulatedFunctionFactory factory = factoryHolder.resolveFactory(request.getFactoryType());
        TabulatedFunction source = toFunction(factory, request.getFunction());
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);
        TabulatedFunction derived = operator.derive(source);
        return new DifferentiationResponse(toPayload(request.getFunction().getName(), derived, request.getFactoryType()));
    }

    public EvaluateTabulatedResponse apply(EvaluateTabulatedRequest request) {
        return functionService.evaluateTabulated(request);
    }

    public IntegrationResponse integrate(IntegrationRequest request) {
        return integrationService.integrate(request);
    }

    public TabulatedFunctionPayload insert(InsertPointRequest request) {
        return mutationService.insert(request.getFunction(), request.getX(), request.getY(), request.getFactoryType());
    }

    public TabulatedFunctionPayload remove(RemovePointRequest request) {
        return mutationService.remove(request.getFunction(), request.getIndex(), request.getFactoryType());
    }

    public ByteArrayResource serialize(TabulatedFunctionPayload payload, String format) throws IOException {
        validatePayload(payload);
        String resolvedFormat = normalizeFormat(format);
        TabulatedFunctionFactory factory = factoryHolder.resolveFactory(payload.getFactoryType());
        TabulatedFunction function = toFunction(factory, payload);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        switch (resolvedFormat) {
            case "json" -> FunctionsIO.serializeJson(function, output);
            case "xml" -> FunctionsIO.serializeXml(function, output);
            case "dat" -> FunctionsIO.serializeBinary(function, output);
            default -> throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
        }

        return new ByteArrayResource(output.toByteArray());
    }

    public TabulatedFunctionPayload deserialize(MultipartFile file, String format, String factoryType) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл функции не передан");
        }
        String resolvedFormat = normalizeFormat(format);
        TabulatedFunctionFactory factory = factoryHolder.resolveFactory(factoryType);

        try (ByteArrayInputStream input = new ByteArrayInputStream(file.getBytes())) {
            TabulatedFunction function = switch (resolvedFormat) {
                case "json" -> FunctionsIO.deserializeJson(input);
                case "xml" -> FunctionsIO.deserializeXml(input);
                case "dat" -> FunctionsIO.deserializeBinary(input);
                default -> throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
            };
            String name = stripExtension(file.getOriginalFilename());
            return toPayload(name, function, factoryType);
        }
    }

    public TabulatedFunctionResponse toResponse(FunctionCreationResult result, String factoryType) {
        TabulatedFunctionResponse response = new TabulatedFunctionResponse();
        response.setId(result.getFunction().getId());
        response.setName(result.getFunction().getName());
        response.setFactoryType(factoryHolder.resolveKey(factoryType));
        response.setPoints(toPoints(result.getXValues(), result.getYValues()));
        return response;
    }

    public List<TabulatedPointDTO> toPoints(double[] xValues, double[] yValues) {
        if (xValues == null || yValues == null || xValues.length != yValues.length) {
            throw new IllegalArgumentException("Некорректные массивы точек для ответа");
        }
        List<TabulatedPointDTO> points = new ArrayList<>();
        for (int i = 0; i < xValues.length; i++) {
            points.add(new TabulatedPointDTO(xValues[i], yValues[i]));
        }
        return points;
    }

    private TabulatedFunctionPayload toPayload(String name, TabulatedFunction function, String factoryKey) {
        TabulatedFunctionPayload response = new TabulatedFunctionPayload();
        response.setName(name);
        response.setXValues(function.getXValues());
        response.setYValues(function.getYValues());
        response.setFactoryType(factoryHolder.resolveKey(factoryKey));
        response.setInsertable(function instanceof Insertable);
        response.setRemovable(function instanceof Removable);
        return response;
    }

    private TabulatedFunction toFunction(TabulatedFunctionFactory factory, TabulatedFunctionPayload payload) {
        validatePayload(payload);
        return factory.create(payload.getXValues(), payload.getYValues());
    }

    private void validatePayload(TabulatedFunctionPayload payload) {
        if (payload == null || payload.getXValues() == null || payload.getYValues() == null) {
            throw new IllegalArgumentException("Функция должна содержать массивы X и Y");
        }
        if (payload.getXValues().length != payload.getYValues().length) {
            throw new IllegalArgumentException("Количество X и Y значений должно совпадать");
        }
    }

    private String normalizeFormat(String format) {
        String normalized = (format == null ? "json" : format).toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "json", "xml", "dat" -> normalized;
            default -> throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
        };
    }

    private String stripExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return filename;
        }
        return filename.substring(0, filename.lastIndexOf('.'));
    }
}
