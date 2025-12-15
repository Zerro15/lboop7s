package com.example.lab5.ui;

import com.example.lab5.framework.dto.TabulatedFunctionPayload;
import com.example.lab5.framework.service.TabulatedFunctionFactoryHolder;
import com.example.lab5.functions.FunctionsIO;
import com.example.lab5.functions.Insertable;
import com.example.lab5.functions.Removable;
import com.example.lab5.functions.TabulatedFunction;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

@RestController
@RequestMapping("/ui/api/tabulated-functions/files")
public class TabulatedFunctionFileUiApiController {

    private final TabulatedFunctionFactoryHolder factoryHolder;

    public TabulatedFunctionFileUiApiController(TabulatedFunctionFactoryHolder factoryHolder) {
        this.factoryHolder = factoryHolder;
    }

    @PostMapping(value = "/serialize", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> serialize(@RequestParam String format,
                                                       @RequestBody TabulatedFunctionPayload payload) throws IOException {
        TabulatedFunction function = toFunction(payload);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String normalized = normalizeFormat(format);
        MediaType mediaType = mediaTypeFor(normalized);
        String extension = extensionFor(normalized);
        switch (normalized) {
            case "json":
                FunctionsIO.serializeJson(function, baos);
                break;
            case "xml":
                FunctionsIO.serializeXml(function, baos);
                break;
            case "dat":
                FunctionsIO.serializeBinary(function, baos);
                break;
            default:
                throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
        }

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename((payload.getName() == null ? "function" : payload.getName()) + extension)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @PostMapping(value = "/deserialize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TabulatedFunctionPayload> deserialize(@RequestParam String format,
                                                                @RequestParam("file") MultipartFile file) throws IOException {
        String normalized = normalizeFormat(format);
        TabulatedFunction function;
        try (var inputStream = file.getInputStream()) {
            switch (normalized) {
                case "json":
                    function = FunctionsIO.deserializeJson(inputStream);
                    break;
                case "xml":
                    function = FunctionsIO.deserializeXml(inputStream);
                    break;
                case "dat":
                    function = FunctionsIO.deserializeBinary(inputStream);
                    break;
                default:
                    throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
            }
        }

        TabulatedFunctionPayload payload = new TabulatedFunctionPayload();
        payload.setName(file.getOriginalFilename());
        payload.setXValues(function.getXValues());
        payload.setYValues(function.getYValues());
        payload.setFactoryType(factoryHolder.getActiveKey());
        payload.setInsertable(function instanceof Insertable);
        payload.setRemovable(function instanceof Removable);

        return ResponseEntity.ok(payload);
    }

    private String normalizeFormat(String format) {
        if (format == null || format.isBlank()) {
            throw new IllegalArgumentException("Формат файла обязателен");
        }
        return format.toLowerCase(Locale.ROOT);
    }

    private MediaType mediaTypeFor(String format) {
        return switch (format) {
            case "json" -> MediaType.APPLICATION_JSON;
            case "xml" -> MediaType.APPLICATION_XML;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private String extensionFor(String format) {
        return switch (format) {
            case "json" -> ".json";
            case "xml" -> ".xml";
            default -> ".dat";
        };
    }

    private TabulatedFunction toFunction(TabulatedFunctionPayload payload) {
        if (payload == null || payload.getXValues() == null || payload.getYValues() == null) {
            throw new IllegalArgumentException("Функция должна содержать X и Y для сохранения");
        }
        return new TabulatedFunction(payload.getXValues(), payload.getYValues());
    }
}
