package com.example.lab5.ui;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class TabulatedFunctionPageController {

    @GetMapping("/")
    public String redirectToUi() {
        return "redirect:/ui/tabulated-functions";
    }

    @GetMapping("/ui/tabulated-functions")
    public ResponseEntity<byte[]> getTabulatedFunctionPage() throws IOException {
        ClassPathResource page = new ClassPathResource("static/ui/tabulated-functions.html");
        byte[] body = StreamUtils.copyToByteArray(page.getInputStream());
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .body(body);
    }
}
