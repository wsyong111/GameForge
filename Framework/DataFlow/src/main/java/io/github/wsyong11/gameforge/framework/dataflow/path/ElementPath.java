package io.github.wsyong11.gameforge.framework.dataflow.path;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Objects;

public interface ElementPath {
	@NotNull
	@UnmodifiableView
	List<Element> match(@NotNull Element element);

	default boolean find(@NotNull Element element) {
		Objects.requireNonNull(element, "element is null");
		return !this.match(element).isEmpty();
	}

	@Nullable
	default Element matchFirst(@NotNull Element element) {
		Objects.requireNonNull(element, "element is null");

		List<Element> list = this.match(element);
		return list.isEmpty() ? null : list.get(0);
	}

	@NotNull
	String toString();
}
