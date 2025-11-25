package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ElementPredicate {
	@NotNull
	static ElementPredicate withField(@NotNull String field) {
		return (fieldName, root, current) -> field.equals(fieldName);
	}

	boolean test(@NotNull String fieldName, @NotNull Element root, @NotNull Element current);

	@Nullable
	default String getExpression() {
		return null;
	}
}
