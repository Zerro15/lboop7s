package com.example.lab5.framework.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Обрабатывает веб-запросы к пользовательскому интерфейсу и перенаправляет их на статические ресурсы.
 */
@Controller
public class UiPageController {

    @GetMapping({"/", "/ui", "/ui/", "/index"})
    public String index() {
        return "forward:/index.html";
    }
}
