package com.example.lab5.ui;

import com.example.lab5.framework.dto.FactoryStateResponse;
import com.example.lab5.framework.dto.TabulatedFactoryDTO;
import com.example.lab5.framework.service.MathFunctionService;
import com.example.lab5.framework.service.TabulatedFunctionFactoryHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui")
public class TabulatedFunctionPageController {

    private final MathFunctionService mathFunctionService;
    private final TabulatedFunctionFactoryHolder factoryHolder;

    public TabulatedFunctionPageController(MathFunctionService mathFunctionService,
                                           TabulatedFunctionFactoryHolder factoryHolder) {
        this.mathFunctionService = mathFunctionService;
        this.factoryHolder = factoryHolder;
    }

    @GetMapping("/tabulated-functions")
    public String tabulatedFunctions(Model model) {
        FactoryStateResponse factoryState = factoryHolder.describeState();
        model.addAttribute("mathFunctions", mathFunctionService.getAllMathFunctions());
        model.addAttribute("factoryTypes", factoryState.getFactories());
        model.addAttribute("factoryState", factoryState);
        String activeLabel = factoryState.getFactories().stream()
                .filter(TabulatedFactoryDTO::isActive)
                .map(TabulatedFactoryDTO::getLabel)
                .findFirst()
                .orElse(factoryState.getActiveKey());
        model.addAttribute("activeFactoryLabel", activeLabel);
        return "tabulated-functions";
    }
}
