package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.FactoryStateResponse;
import com.example.lab5.framework.dto.TabulatedFactoryDTO;
import com.example.lab5.functions.ArrayTabulatedFunctionFactory;
import com.example.lab5.functions.LinkedListTabulatedFunctionFactory;
import com.example.lab5.functions.TabulatedFunctionFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class TabulatedFunctionFactoryHolder {

    private final Map<String, FactoryDefinition> registry = new LinkedHashMap<>();
    private String activeKey;
    private TabulatedFunctionFactory activeFactory;

    public TabulatedFunctionFactoryHolder() {
        register("array", "ArrayTabulatedFunctionFactory", "Хранение точек в массиве", ArrayTabulatedFunctionFactory::new);
        register("linked_list", "LinkedListTabulatedFunctionFactory", "Хранение точек в связном списке", LinkedListTabulatedFunctionFactory::new);
        activate("array");
    }

    private void register(String key, String label, String description, Supplier<TabulatedFunctionFactory> supplier) {
        registry.put(key, new FactoryDefinition(key, label, description, supplier));
    }

    public synchronized void activate(String key) {
        FactoryDefinition definition = registry.get(key);
        if (definition == null) {
            throw new IllegalArgumentException("Неизвестный тип фабрики: " + key);
        }
        activeKey = key;
        activeFactory = definition.supplier().get();
    }

    public synchronized TabulatedFunctionFactory getActiveFactory() {
        return activeFactory;
    }

    public synchronized String getActiveKey() {
        return activeKey;
    }

    public synchronized TabulatedFunctionFactory resolveFactory(String requestedKey) {
        if (requestedKey == null || requestedKey.isBlank() || requestedKey.equals(activeKey)) {
            return activeFactory;
        }
        FactoryDefinition definition = registry.get(requestedKey);
        if (definition == null) {
            throw new IllegalArgumentException("Неизвестный тип фабрики: " + requestedKey);
        }
        return definition.supplier().get();
    }

    public synchronized String resolveKey(String requestedKey) {
        if (requestedKey == null || requestedKey.isBlank()) {
            return activeKey;
        }
        if (!registry.containsKey(requestedKey)) {
            throw new IllegalArgumentException("Неизвестный тип фабрики: " + requestedKey);
        }
        return requestedKey;
    }

    public synchronized List<TabulatedFactoryDTO> getAvailableFactories() {
        return registry.values().stream()
                .map(def -> toDto(def.key))
                .collect(Collectors.toList());
    }

    public synchronized FactoryStateResponse describeState() {
        FactoryStateResponse response = new FactoryStateResponse();
        response.setActiveKey(activeKey);
        response.setFactories(getAvailableFactories());
        return response;
    }

    private TabulatedFactoryDTO toDto(String key) {
        FactoryDefinition definition = registry.get(key);
        TabulatedFactoryDTO dto = new TabulatedFactoryDTO();
        dto.setKey(key);
        dto.setLabel(definition.label());
        dto.setDescription(definition.description());
        dto.setActive(key.equals(activeKey));
        return dto;
    }

    private record FactoryDefinition(String key, String label, String description,
                                    Supplier<TabulatedFunctionFactory> supplier) {
    }
}
