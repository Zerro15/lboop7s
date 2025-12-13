package com.example.lab5.ui.config;

import com.example.lab5.functions.DefaultTabulatedFunctionFactory;
import com.example.lab5.functions.TabulatedFunctionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfig {
    @Bean
    public TabulatedFunctionFactory tabulatedFunctionFactory() {
        return new DefaultTabulatedFunctionFactory();
    }
}
