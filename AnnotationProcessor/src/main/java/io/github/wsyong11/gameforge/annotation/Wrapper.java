package io.github.wsyong11.gameforge.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface Wrapper {
	@NotNull
	Class<?> value();
}
