package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.FactoryActivationRequest;
import com.example.lab5.framework.dto.FactoryStateResponse;
import com.example.lab5.framework.service.TabulatedFunctionFactoryHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ui/api/factories")
public class FactoryUiController {

    private final TabulatedFunctionFactoryHolder factoryHolder;

    public FactoryUiController(TabulatedFunctionFactoryHolder factoryHolder) {
        this.factoryHolder = factoryHolder;
    }

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FactoryStateResponse getInfo() {
        return factoryHolder.describeState();
    }

    @PostMapping("/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FactoryStateResponse activate(@RequestBody FactoryActivationRequest request) {
        factoryHolder.activate(request.getKey());
        return factoryHolder.describeState();
    }
}
