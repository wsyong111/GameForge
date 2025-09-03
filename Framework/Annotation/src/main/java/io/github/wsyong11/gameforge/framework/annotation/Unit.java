package io.github.wsyong11.gameforge.framework.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unit {
	@NotNull
	Type value();

	enum Type {

	}
}
