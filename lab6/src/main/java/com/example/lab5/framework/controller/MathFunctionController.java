package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.CompositeCreateRequest;
import com.example.lab5.framework.dto.CompositeDeleteRequest;
import com.example.lab5.framework.dto.CompositeRenameRequest;
import com.example.lab5.framework.dto.MathFunctionDTO;
import com.example.lab5.framework.dto.MathFunctionGroupsResponse;
import com.example.lab5.framework.dto.PreviewRequest;
import com.example.lab5.framework.dto.PreviewResponse;
import com.example.lab5.framework.service.MathFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/math-functions")
public class MathFunctionController {

    @Autowired
    private MathFunctionService mathFunctionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<MathFunctionDTO> getAllMathFunctions() {
        return mathFunctionService.getAllMathFunctions();
    }

    @GetMapping("/map")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<String, MathFunctionDTO> getFunctionMap() {
        return mathFunctionService.getFunctionMap();
    }

    @GetMapping("/groups")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public MathFunctionGroupsResponse getFunctionGroups() {
        return mathFunctionService.describeGroups();
    }

    @PostMapping("/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public PreviewResponse previewMathFunction(@RequestBody PreviewRequest request) {
        return mathFunctionService.previewMathFunction(
                request.getMathFunctionKey(),
                request.getPointsCount(),
                request.getLeftBound(),
                request.getRightBound()
        );
    }

    @PostMapping("/composite")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public MathFunctionGroupsResponse createComposite(@RequestBody CompositeCreateRequest request) {
        mathFunctionService.createComposite(request.getName(), request.getOuterKey(), request.getInnerKey());
        return mathFunctionService.describeGroups();
    }

    @PostMapping("/composite/rename")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public MathFunctionGroupsResponse renameComposite(@RequestBody CompositeRenameRequest request) {
        mathFunctionService.renameComposite(request.getOldName(), request.getNewName());
        return mathFunctionService.describeGroups();
    }

    @PostMapping("/composite/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public MathFunctionGroupsResponse deleteComposite(@RequestBody CompositeDeleteRequest request) {
        mathFunctionService.deleteComposite(request.getName());
        return mathFunctionService.describeGroups();
    }
}