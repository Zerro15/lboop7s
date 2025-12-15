package com.example.lab5.ui;

import com.example.lab5.framework.dto.IntegrationRequest;
import com.example.lab5.framework.dto.IntegrationResponse;
import com.example.lab5.framework.service.TabulatedIntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/api/tabulated-functions/integration")
public class TabulatedFunctionIntegrationUiApiController {

    private final TabulatedIntegrationService integrationService;

    public TabulatedFunctionIntegrationUiApiController(TabulatedIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping
    public ResponseEntity<IntegrationResponse> integrate(@RequestBody IntegrationRequest request) {
        return ResponseEntity.ok(integrationService.integrate(request));
    }
}
