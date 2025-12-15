package com.example.lab5.ui;

import com.example.lab5.framework.service.MathFunctionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ui")
public class TabulatedFunctionPageController {

    private final MathFunctionService mathFunctionService;

    public TabulatedFunctionPageController(MathFunctionService mathFunctionService) {
        this.mathFunctionService = mathFunctionService;
    }

    @GetMapping("/tabulated-functions")
    public String tabulatedFunctions(Model model) {
        model.addAttribute("mathFunctions", mathFunctionService.getAllMathFunctions());
        model.addAttribute("factoryTypes", List.of("array", "linked_list"));
        return "tabulated-functions";
    }
}
