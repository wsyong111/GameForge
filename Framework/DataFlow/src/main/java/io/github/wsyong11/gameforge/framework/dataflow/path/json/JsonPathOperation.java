package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.dataflow.element.ArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.ObjectElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface JsonPathOperation {
	@NotNull
	List<Element> apply(@NotNull Context context);

	interface Context {
		@NotNull
		Element getRoot();

		@NotNull
		Element getCurrent();

		@Nullable
		String getFieldName(); // Root, current is list -> null

		int getFieldIndex(); // current is not list -> -1
	}

	class Root implements JsonPathOperation {
		@NotNull
		@Override
		public List<Element> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");
			return List.of(context.getRoot());
		}
	}

	class Self implements JsonPathOperation {
		@NotNull
		@Override
		public List<Element> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");
			return List.of(context.getCurrent());
		}
	}

	class Wildcard implements JsonPathOperation {
		@NotNull
		@Override
		public List<Element> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");

			Element element = context.getCurrent();
			if (element instanceof ArrayElement array)
				return array.asList();

			if (element instanceof ObjectElement object)
				return List.copyOf(object.asMap().values());

			return List.of();
		}
	}

	class Slice implements JsonPathOperation {
		private final int start;
		private final int end;
		private final int step;

		public Slice(int start, int end, int step) {
			this.start = start;
			this.end = end;
			this.step = step;
		}

		@NotNull
		@Override
		public List<Element> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");

			Element element = context.getCurrent();
			if (!(element instanceof ArrayElement array))
				return List.of();


		}
	}
}
