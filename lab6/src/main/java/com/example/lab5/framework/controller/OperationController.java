package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.DifferentiationRequest;
import com.example.lab5.framework.dto.DifferentiationResponse;
import com.example.lab5.framework.dto.OperationRequest;
import com.example.lab5.framework.dto.OperationResponse;
import com.example.lab5.framework.service.OperationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/operations")
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping("/elementary")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public OperationResponse applyOperation(@RequestBody OperationRequest request) {
        return operationService.apply(request);
    }

    @PostMapping("/differentiate")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public DifferentiationResponse differentiate(@RequestBody DifferentiationRequest request) {
        return operationService.differentiate(request);
    }
}
