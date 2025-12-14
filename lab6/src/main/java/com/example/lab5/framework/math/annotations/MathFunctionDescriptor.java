package com.example.lab5.framework.math.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MathFunctionDescriptor {
    String key();
    String localizedName();
    String description() default "";
    String example() default "";
    String category() default "";
    int priority() default 100;
}
