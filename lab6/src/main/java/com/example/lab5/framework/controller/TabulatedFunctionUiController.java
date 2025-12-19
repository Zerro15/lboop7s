package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.*;
import com.example.lab5.framework.service.FunctionService;
import com.example.lab5.framework.service.TabulatedFunctionFactoryHolder;
import com.example.lab5.framework.service.TabulatedFunctionUiService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

@RestController
@RequestMapping("/ui/api")
public class TabulatedFunctionUiController {

    private final FunctionService functionService;
    private final TabulatedFunctionUiService uiService;
    private final TabulatedFunctionFactoryHolder factoryHolder;

    public TabulatedFunctionUiController(FunctionService functionService,
                                         TabulatedFunctionUiService uiService,
                                         TabulatedFunctionFactoryHolder factoryHolder) {
        this.functionService = functionService;
        this.uiService = uiService;
        this.factoryHolder = factoryHolder;
    }

    @PostMapping("/tabulated-functions/arrays")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TabulatedFunctionResponse createFromArrays(@RequestBody CreateFromArraysRequest request) {
        var result = functionService.createFromArrays(
                request.getUserId(),
                request.getName(),
                request.getPoints(),
                request.getFactoryType()
        );
        return uiService.toResponse(result, request.getFactoryType());
    }

    @PostMapping("/tabulated-functions/math-function")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TabulatedFunctionResponse createFromMath(@RequestBody CreateFromMathRequest request) {
        var result = functionService.createFromMathFunction(
                request.getUserId(),
                request.getName(),
                request.getMathFunctionKey(),
                request.getPointsCount(),
                request.getLeftBound(),
                request.getRightBound(),
                request.getFactoryType()
        );
        return uiService.toResponse(result, request.getFactoryType());
    }

    // ИЗМЕНИЛ: /basic-operations вместо /operations
    @PostMapping("/tabulated-functions/basic-operations")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public OperationResponse calculate(@RequestBody OperationRequest request) {
        return uiService.operate(request);
    }

    // ИЗМЕНИЛ: /basic-differentiate вместо /differential
    @PostMapping("/tabulated-functions/basic-differentiate")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public DifferentiationResponse differentiate(@RequestBody DifferentiationRequest request) {
        return uiService.differentiate(request);
    }

    // ИЗМЕНИЛ: /basic-integrate вместо /integration
    @PostMapping("/tabulated-functions/basic-integrate")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public IntegrationResponse integrate(@RequestBody IntegrationRequest request) {
        return uiService.integrate(request);
    }

    @PostMapping("/tabulated-functions/apply")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public EvaluateTabulatedResponse apply(@RequestBody EvaluateTabulatedRequest request) {
        return uiService.apply(request);
    }

    @PostMapping("/tabulated-functions/insert")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TabulatedFunctionPayload insert(@RequestBody InsertPointRequest request) {
        return uiService.insert(request);
    }

    @PostMapping("/tabulated-functions/remove")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TabulatedFunctionPayload remove(@RequestBody RemovePointRequest request) {
        return uiService.remove(request);
    }

    // ИЗМЕНИЛ: добавил /basic/ перед files
    @PostMapping(value = "/tabulated-functions/basic/files/serialize", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ByteArrayResource> serialize(@RequestParam(defaultValue = "json") String format,
                                                       @RequestBody TabulatedFunctionPayload payload) throws IOException {
        ByteArrayResource resource = uiService.serialize(payload, format);
        String contentType = resolveContentType(format);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tabulated-function." + extension(format))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    // ИЗМЕНИЛ: добавил /basic/ перед files
    @PostMapping(value = "/tabulated-functions/basic/files/deserialize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TabulatedFunctionPayload deserialize(@RequestParam(defaultValue = "json") String format,
                                                @RequestParam("file") MultipartFile file,
                                                @RequestParam(value = "factoryType", required = false) String factoryType) throws IOException {
        String resolvedFactory = factoryType == null || factoryType.isBlank()
                ? factoryHolder.getActiveKey()
                : factoryType;
        return uiService.deserialize(file, format, resolvedFactory);
    }

    private String resolveContentType(String format) {
        String normalized = extension(format);
        return switch (normalized) {
            case "json" -> MediaType.APPLICATION_JSON_VALUE;
            case "xml" -> MediaType.APPLICATION_XML_VALUE;
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }

    private String extension(String format) {
        String normalized = (format == null ? "json" : format).toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "json", "xml", "dat" -> normalized;
            default -> "json";
        };
    }
}