package com.example.lab5.manual.functions;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Реестр доступных математических функций с локализованными именами.
 */
public final class MathFunctionRegistry {
    private static final MathFunctionRegistry INSTANCE = new MathFunctionRegistry();

    private final Map<String, RegistryItem> functions = new LinkedHashMap<>();

    private MathFunctionRegistry() {
        discoverAnnotatedFunctions("com.example.lab5.manual.functions.impl");
    }

    public static MathFunctionRegistry getInstance() {
        return INSTANCE;
    }

    public synchronized void registerCustom(String name, MathFunction function, int priority) {
        functions.put(name, new RegistryItem(name, function, priority));
    }

    public synchronized Map<String, MathFunction> getFunctions() {
        Map<String, MathFunction> ordered = new LinkedHashMap<>();
        sortedItems().forEach(item -> ordered.put(item.name, item.function));
        return ordered;
    }

    public synchronized List<String> listLocalizedNames() {
        List<String> names = new ArrayList<>();
        sortedItems().forEach(item -> names.add(item.name));
        return names;
    }

    private List<RegistryItem> sortedItems() {
        List<RegistryItem> list = new ArrayList<>(functions.values());
        list.sort(Comparator.comparingInt((RegistryItem i) -> i.priority)
                .thenComparing(i -> i.name));
        return list;
    }

    private void discoverAnnotatedFunctions(String basePackage) {
        String path = basePackage.replace('.', '/');
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if (!"file".equals(url.getProtocol())) {
                    continue;
                }
                File dir = new File(url.toURI());
                File[] files = dir.listFiles((f, name) -> name.endsWith(".class"));
                if (files == null) {
                    continue;
                }
                for (File file : files) {
                    String className = basePackage + '.' + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    if (!MathFunction.class.isAssignableFrom(clazz) || !clazz.isAnnotationPresent(MathFunctionInfo.class)) {
                        continue;
                    }
                    MathFunctionInfo info = clazz.getAnnotation(MathFunctionInfo.class);
                    MathFunction function = (MathFunction) clazz.getDeclaredConstructor().newInstance();
                    registerCustom(info.name(), function, info.priority());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить функции по аннотациям", e);
        }
    }

    private static final class RegistryItem {
        private final String name;
        private final MathFunction function;
        private final int priority;

        private RegistryItem(String name, MathFunction function, int priority) {
            this.name = name;
            this.function = function;
            this.priority = priority;
        }
    }
}
