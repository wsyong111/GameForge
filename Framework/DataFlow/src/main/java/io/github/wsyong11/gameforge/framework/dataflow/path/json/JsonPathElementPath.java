package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.MissingElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.NullElement;
import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Internal
public class JsonPathElementPath implements ElementPath {
	@NotNull
	@UnmodifiableView
	public static List<Element> matchStatic(@Nullable Element root, @NotNull Element element, @NotNull List<JsonPathOperation> operations) {
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(operations, "operations is null");

		if (element instanceof MissingElement || element instanceof NullElement)
			return List.of();

		Element rootElement = root != null ? root : element;

		List<JsonPathOperation.ElementValue> currentElements = List.of(
			JsonPathOperation.ElementValue.ofField(element, ""));

		for (JsonPathOperation operation : operations) {
			List<JsonPathOperation.ElementValue> resultElements = new ArrayList<>();
			for (JsonPathOperation.ElementValue current : currentElements) {
				List<JsonPathOperation.ElementValue> result = operation.apply(new SimpleJsonPathContext(rootElement, current));
				Objects.requireNonNull(result, "result is null");
				resultElements.addAll(result);
			}

			currentElements = resultElements
				.stream()
				.filter(value -> !(value.getElement() instanceof MissingElement))
				.toList();
		}

		return currentElements
			.stream()
			.map(JsonPathOperation.ElementValue::getElement)
			.toList();
	}

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
		return matchStatic(element, element, this.operations);
	}

	@NotNull
	@Override
	public String toString() {
		return this.operations.stream().map(JsonPathOperation::toString).collect(Collectors.joining());
	}
}
