package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import io.github.wsyong11.gameforge.util.reflect.ReflectUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TypeResolver<T> {
	@NotNull
	Type resolve(@NotNull T value);

	@NotNull
	static <V> TypeResolver<V> collection(@NotNull Function<V, Collection<?>> getter) {
		Objects.requireNonNull(getter, "getter is null");
		return v -> ReflectUtils.findCommonSuperclass(getter
			.apply(v)
			.stream()
			.<Class<?>>map(Object::getClass)
			.toList());
	}
}
