package io.github.wsyong11.gameforge.framework.context.annotation;

import io.github.wsyong11.gameforge.framework.annotation.CallerSensitive;
import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Documented
@ThreadSensitive
@CallerSensitive
public @interface UsingContext {
	boolean require() default false;
}
