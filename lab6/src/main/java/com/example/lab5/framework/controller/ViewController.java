package com.example.lab5.framework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping(value = {"/", "/login", "/dashboard", "/profile"})
    public String index() {
        return "tabulated-functions"; // вернет index.html из templates/
    }
}
