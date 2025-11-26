package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class SimpleJsonPathContext implements JsonPathOperation.Context {
	private final Element root;
	private final JsonPathOperation.ElementValue current;

	SimpleJsonPathContext(@NotNull Element root, @NotNull JsonPathOperation.ElementValue current) {
		Objects.requireNonNull(root, "root is null");
		Objects.requireNonNull(current, "current is null");

		this.root = root;
		this.current = current;
	}

	@NotNull
	@Override
	public Element getRoot() {
		return this.root;
	}

	@NotNull
	@Override
	public JsonPathOperation.ElementValue getCurrent() {
		return this.current;
	}
}
