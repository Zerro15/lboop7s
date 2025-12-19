package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.*;
import com.example.lab5.framework.service.MathFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")  // ИЗМЕНИТЕ эту строку
public class MathFunctionController {

    private final MathFunctionService mathFunctionService;

    @Autowired
    public MathFunctionController(MathFunctionService mathFunctionService) {
        this.mathFunctionService = mathFunctionService;
    }

    // ДОБАВЬТЕ этот метод для фронтенда
    @GetMapping("/math/all-functions")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<MathFunctionDTO> getAllMathFunctions() {
        return mathFunctionService.getAllMathFunctions();
    }

    // Переименуйте старый метод, чтобы избежать конфликта
    @GetMapping("/math-functions/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<MathFunctionDTO> getAllMathFunctionsInternal() {
        return mathFunctionService.getAllMathFunctions();
    }

    @GetMapping("/math-functions/map")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<String, MathFunctionDTO> getFunctionMap() {
        return mathFunctionService.getFunctionMap();
    }

    @GetMapping("/math-functions/groups")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public MathFunctionGroupsResponse getFunctionGroups() {
        return mathFunctionService.describeGroups();
    }

    @PostMapping("/math-functions/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public PreviewResponse previewMathFunction(@RequestBody PreviewRequest request) {
        return mathFunctionService.previewMathFunction(
                request.getMathFunctionKey(),
                request.getPointsCount(),
                request.getLeftBound(),
                request.getRightBound()
        );
    }

    @PostMapping("/math-functions/composite")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public MathFunctionGroupsResponse createComposite(@RequestBody CompositeCreateRequest request) {
        mathFunctionService.createComposite(
                request.getName(),
                request.getOuterKey(),
                request.getInnerKey()
        );
        return mathFunctionService.describeGroups();
    }

    @PostMapping("/math-functions/composite/rename")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public MathFunctionGroupsResponse renameComposite(@RequestBody CompositeRenameRequest request) {
        mathFunctionService.renameComposite(
                request.getOldName(),
                request.getNewName()
        );
        return mathFunctionService.describeGroups();
    }

    @DeleteMapping("/math-functions/composite")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public MathFunctionGroupsResponse deleteComposite(@RequestBody CompositeDeleteRequest request) {
        mathFunctionService.deleteComposite(request.getName());
        return mathFunctionService.describeGroups();
    }
}