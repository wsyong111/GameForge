package io.github.wsyong11.gameforge.framework.dataflow.path;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonPathElementPath implements ElementPath {
	private final List<Operation> operations;

	public JsonPathElementPath(@NotNull List<Operation> operations) {
		Objects.requireNonNull(operations, "operations is null");
		this.operations = List.copyOf(operations);
	}

	@NotNull
	@Override
	public String toString() {
		return this.operations.stream().map(Operation::toString).collect(Collectors.joining());
	}

	public interface Operation {
		@NotNull
		ElementPath apply(@NotNull ElementPath root, @NotNull ElementPath current);
	}

	public static class OpRoot implements Operation {
		@NotNull
		@Override
		public ElementPath apply(@NotNull ElementPath root, @NotNull ElementPath current) {
			Objects.requireNonNull(root, "root is null");
			return root;
		}

		@Override
		public String toString() {
			return "$";
		}
	}
}
