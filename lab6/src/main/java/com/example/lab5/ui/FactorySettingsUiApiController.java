package com.example.lab5.ui;

import com.example.lab5.framework.dto.ActivateFactoryRequest;
import com.example.lab5.framework.dto.FactoryStateResponse;
import com.example.lab5.framework.service.TabulatedFunctionFactoryHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ui/api/factory-management") // ИЗМЕНИЛ: уникальный путь
public class FactorySettingsUiApiController {

    private final TabulatedFunctionFactoryHolder factoryHolder;

    public FactorySettingsUiApiController(TabulatedFunctionFactoryHolder factoryHolder) {
        this.factoryHolder = factoryHolder;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FactoryStateResponse getState() {
        return factoryHolder.describeState();
    }

    @PostMapping("/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FactoryStateResponse activate(@RequestBody ActivateFactoryRequest request) {
        factoryHolder.activate(request.getKey());
        return factoryHolder.describeState();
    }
}