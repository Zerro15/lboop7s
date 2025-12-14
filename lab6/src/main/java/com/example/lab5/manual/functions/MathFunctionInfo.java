package com.example.lab5.manual.functions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Аннотация для автоматического поиска простых функций.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MathFunctionInfo {
    /**
     * Локализованное имя функции.
     */
    String name();

    /**
     * Приоритет отображения (меньшее значение — выше в списке).
     */
    int priority() default 0;
}
