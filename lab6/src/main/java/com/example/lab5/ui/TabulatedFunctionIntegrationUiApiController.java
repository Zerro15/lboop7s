package com.example.lab5.ui;

import com.example.lab5.framework.dto.IntegrationRequest;
import com.example.lab5.framework.dto.IntegrationResponse;
import com.example.lab5.framework.service.TabulatedIntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ui/api/tabulated-functions/advanced") // Тот же префикс, что и у differentiation
public class TabulatedFunctionIntegrationUiApiController {

    private final TabulatedIntegrationService integrationService;

    public TabulatedFunctionIntegrationUiApiController(TabulatedIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping("/integrate") // Теперь путь: /ui/api/tabulated-functions/advanced/integrate
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<IntegrationResponse> integrate(@RequestBody IntegrationRequest request) {
        return ResponseEntity.ok(integrationService.integrate(request));
    }
}