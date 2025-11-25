package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonPathElementPath implements ElementPath {
	private final List<JsonPathOperation> operations;

	public JsonPathElementPath(@NotNull List<JsonPathOperation> operations) {
		Objects.requireNonNull(operations, "operations is null");
		this.operations = List.copyOf(operations);
	}

	@NotNull
	@UnmodifiableView
	@Override
	public List<Element> match(@NotNull Element element) {
		Objects.requireNonNull(element, "element is null");


	}

	@NotNull
	@Override
	public String toString() {
		return this.operations.stream().map(JsonPathOperation::toString).collect(Collectors.joining());
	}

}
