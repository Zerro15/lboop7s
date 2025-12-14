package com.example.lab5.framework.math.functions;

import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AnnotatedMathFunction {
    private final MathFunctionDescriptor descriptor;
    private final SimpleMathFunction function;

    public AnnotatedMathFunction(MathFunctionDescriptor descriptor, SimpleMathFunction function) {
        this.descriptor = descriptor;
        this.function = function;
    }

    public MathFunctionDescriptor getDescriptor() {
        return descriptor;
    }

    public SimpleMathFunction getFunction() {
        return function;
    }

    private static final AtomicReference<List<AnnotatedMathFunction>> CACHE = new AtomicReference<>();

    public static List<AnnotatedMathFunction> discoveryCache() {
        if (CACHE.get() == null) {
            CACHE.set(discoverAnnotatedFunctions());
        }
        return CACHE.get();
    }

    private static List<AnnotatedMathFunction> discoverAnnotatedFunctions() {
        Set<Class<?>> candidates = discoverClasses("com.example.lab5.framework.math.functions");
        List<AnnotatedMathFunction> result = new ArrayList<>();
        for (Class<?> clazz : candidates) {
            if (!SimpleMathFunction.class.isAssignableFrom(clazz)) {
                continue;
            }
            MathFunctionDescriptor descriptor = clazz.getAnnotation(MathFunctionDescriptor.class);
            if (descriptor == null) {
                continue;
            }
            try {
                SimpleMathFunction function = (SimpleMathFunction) clazz.getDeclaredConstructor().newInstance();
                result.add(new AnnotatedMathFunction(descriptor, function));
            } catch (Exception ignored) {
                // пропускаем некорректные классы
            }
        }
        return result;
    }

    private static Set<Class<?>> discoverClasses(String basePackage) {
        String path = basePackage.replace('.', '/');
        Set<Class<?>> classes = new TreeSet<>((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();
                if (Objects.equals(protocol, "jar")) {
                    collectFromJar(classes, url, path);
                } else if (Objects.equals(protocol, "file")) {
                    collectFromDirectory(classes, url.getPath(), basePackage, classLoader);
                }
            }
        } catch (IOException ignored) {
            // игнорируем ошибки поиска
        }
        return classes;
    }

    private static void collectFromJar(Set<Class<?>> classes, URL url, String path) {
        try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            try (JarFile jarFile = connection.getJarFile()) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                        loadClass(classes, name);
                    }
                }
            }
        } catch (IOException ignored) {
            // пропускаем проблемы с jar
        }
    }

    private static void collectFromDirectory(Set<Class<?>> classes, String directoryPath, String basePackage, ClassLoader loader) {
        // This simplistic scanner works for exploded builds during development.
        // Jar scanning is handled separately.
        java.io.File directory = new java.io.File(directoryPath);
        if (!directory.exists()) {
            return;
        }
        java.io.File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (java.io.File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                collectFromDirectory(classes, file.getPath(), basePackage + "." + fileName, loader);
            } else if (fileName.endsWith(".class")) {
                String className = basePackage + '.' + fileName.substring(0, fileName.length() - 6);
                try {
                    classes.add(Class.forName(className, false, loader));
                } catch (ClassNotFoundException ignored) {
                    // пропускаем отсутствующие классы
                }
            }
        }
    }

    private static void loadClass(Set<Class<?>> classes, String name) {
        String className = name.replace('/', '.').substring(0, name.length() - 6);
        try {
            classes.add(Class.forName(className));
        } catch (ClassNotFoundException ignored) {
            // пропускаем
        }
    }
}
