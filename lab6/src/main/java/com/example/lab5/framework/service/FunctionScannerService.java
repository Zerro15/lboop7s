package com.example.lab5.framework.service;

import com.example.lab5.functions.FunctionProperties;
import com.example.lab5.functions.MathFunction;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

@Service
public class FunctionScannerService {

    private final List<FunctionDescriptor> descriptors;

    public FunctionScannerService() {
        this.descriptors = Collections.unmodifiableList(scanFunctions());
    }

    public List<FunctionDescriptor> getDescriptors() {
        return descriptors;
    }

    public Map<String, MathFunction> getFunctionMap() {
        Map<String, MathFunction> map = new LinkedHashMap<>();
        descriptors.forEach(desc -> map.put(desc.label(), desc.function()));
        return map;
    }

    private List<FunctionDescriptor> scanFunctions() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(MathFunction.class));

        List<FunctionDescriptor> found = new ArrayList<>();
        for (BeanDefinition candidate : scanner.findCandidateComponents("com.example.lab5.functions")) {
            try {
                Class<?> clazz = Class.forName(candidate.getBeanClassName());
                if (!MathFunction.class.isAssignableFrom(clazz) || clazz.isInterface() ||
                        Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }
                FunctionProperties properties = clazz.getAnnotation(FunctionProperties.class);
                if (properties == null) {
                    continue;
                }
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                MathFunction instance = (MathFunction) constructor.newInstance();
                found.add(new FunctionDescriptor(properties.nameLocale(), properties.priority(),
                        instance, clazz.getSimpleName()));
            } catch (Exception ignored) {
                // ignore classes that cannot be instantiated
            }
        }

        found.sort(Comparator.comparingInt(FunctionDescriptor::priority)
                .thenComparing(FunctionDescriptor::label));

        Map<String, FunctionDescriptor> deduplicated = new LinkedHashMap<>();
        for (FunctionDescriptor descriptor : found) {
            deduplicated.putIfAbsent(descriptor.label(), descriptor);
        }
        return new ArrayList<>(deduplicated.values());
    }

    public record FunctionDescriptor(String label, int priority, MathFunction function, String functionType) {
    }
}
