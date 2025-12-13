package com.example.lab5.ui.config;

import com.example.lab5.functions.IdentityFunction;
import com.example.lab5.functions.MathFunction;
import com.example.lab5.functions.SqrFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class MathFunctionConfig {
    @Bean
    public Map<String, MathFunction> localizedFunctions() {
        Map<String, MathFunction> functions = new LinkedHashMap<>();
        functions.put("Квадратичная функция", new SqrFunction());
        functions.put("Тождественная функция", new IdentityFunction());
        return functions;
    }
}
