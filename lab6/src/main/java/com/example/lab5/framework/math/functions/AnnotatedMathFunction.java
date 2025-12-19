package com.example.lab5.framework.math.functions;

import com.example.lab5.framework.math.annotations.MathFunctionDescriptor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

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

    private static final String BASE_PACKAGE = "com.example.lab5.framework.math.functions";

    private static List<AnnotatedMathFunction> discoverAnnotatedFunctions() {
        Set<Class<?>> candidates = discoverClasses(BASE_PACKAGE);
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
        Set<Class<?>> classes = new TreeSet<>((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resolver);
        String pattern = "classpath*:" + ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*.class";

        try {
            Resource[] resources = resolver.getResources(pattern);
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }
                try {
                    MetadataReader metadataReader = readerFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    classes.add(ClassUtils.forName(className, AnnotatedMathFunction.class.getClassLoader()));
                } catch (Exception ignored) {
                    // пропускаем поврежденные или недоступные классы
                }
            }
        } catch (Exception ignored) {
            // игнорируем ошибки поиска ресурсов
        }
        return classes;
    }
}
