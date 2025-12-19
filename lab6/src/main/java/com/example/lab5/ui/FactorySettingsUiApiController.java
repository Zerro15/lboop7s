package com.example.lab5.ui;

import com.example.lab5.framework.dto.ActivateFactoryRequest;
import com.example.lab5.framework.dto.FactoryStateResponse;
import com.example.lab5.framework.service.TabulatedFunctionFactoryHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/api/factories")
public class FactorySettingsUiApiController {

    private final TabulatedFunctionFactoryHolder factoryHolder;

    public FactorySettingsUiApiController(TabulatedFunctionFactoryHolder factoryHolder) {
        this.factoryHolder = factoryHolder;
    }

    @GetMapping
    public FactoryStateResponse getState() {
        return factoryHolder.describeState();
    }

    @PostMapping("/activate")
    public FactoryStateResponse activate(@RequestBody ActivateFactoryRequest request) {
        factoryHolder.activate(request.getKey());
        return factoryHolder.describeState();
    }
}
