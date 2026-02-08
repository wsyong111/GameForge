package io.github.wsyong11.gameforge.framework.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSensitive {
}
