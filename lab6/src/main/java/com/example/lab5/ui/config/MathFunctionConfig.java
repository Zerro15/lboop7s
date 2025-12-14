package com.example.lab5.ui.config;

import com.example.lab5.ui.dto.MathFunctionOption;
import com.example.lab5.ui.math.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class MathFunctionConfig {

    @Bean
    public Map<String, MathFunction> localizedFunctionMap() {
        List<MathFunction> functions = List.of(
                new CosFunction(),
                new ExpFunction(),
                new IdentityFunction(),
                new LogFunction(),
                new SinFunction(),
                new SqrFunction()
        );

        return functions.stream()
                .sorted((a, b) -> a.getLocalizedName().compareToIgnoreCase(b.getLocalizedName()))
                .collect(Collectors.toMap(
                        MathFunction::getLocalizedName,
                        function -> function,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    @Bean
    public List<MathFunctionOption> mathFunctionOptions(Map<String, MathFunction> localizedFunctionMap) {
        return localizedFunctionMap.entrySet().stream()
                .map(entry -> new MathFunctionOption(entry.getKey(), entry.getValue().getKey()))
                .sorted()
                .collect(Collectors.toList());
    }
}
