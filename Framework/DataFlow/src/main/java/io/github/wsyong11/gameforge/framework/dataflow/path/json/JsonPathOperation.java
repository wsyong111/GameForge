package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.dataflow.element.*;
import io.github.wsyong11.gameforge.util.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public interface JsonPathOperation {
	@NotNull
	List<ElementValue> apply(@NotNull Context context);

	@FunctionalInterface
	interface Predicate {
		@NotNull
		static Predicate withField(@NotNull String field) {
			Objects.requireNonNull(field, "field is null");
			return (context) ->
				context.getCurrent() instanceof ElementValue.Field fieldValue &&
					field.equals(fieldValue.getField());
		}

		@NotNull
		static Predicate withFields(@NotNull Collection<String> fields) {
			Objects.requireNonNull(fields, "fields is null");
			return (context) ->
				context.getCurrent() instanceof ElementValue.Field fieldValue &&
					fields.contains(fieldValue.getField());
		}

		@NotNull
		static Predicate withIndex(int index) {
			if (index < 0)
				throw new IllegalArgumentException("Index cannot be negative");

			return (context) ->
				context.getCurrent() instanceof ElementValue.Index indexValue &&
					indexValue.getIndex() == index;
		}

		@NotNull
		static Predicate withIndex(@NotNull Collection<Integer> indexList) {
			Objects.requireNonNull(indexList, "index is null");

			for (Integer index : indexList) {
				Objects.requireNonNull(index, "index is null");
				if (index < 0)
					throw new IllegalArgumentException("Index cannot be negative");
			}

			return (context) ->
				context.getCurrent() instanceof ElementValue.Index indexValue &&
					indexList.contains(indexValue.getIndex());
		}

		@NotNull
		static Predicate withOperation(@NotNull List<JsonPathOperation> operations) {
			Objects.requireNonNull(operations, "operations is null");
			return (context) -> {
				List<Element> elements = JsonPathElementPath.matchStatic(
					context.getRoot(),
					context.getCurrent().getElement(),
					operations
				);

				return !elements.isEmpty() && elements
					.stream()
					.allMatch(element -> {
						if (element instanceof BooleanElement bool)
							return bool.getValue();
						if (element instanceof NumberElement num)
							return num.getAsDouble() != 0;
						if (element instanceof StringElement str)
							return !str.getValue().isEmpty();
						return true;
					});
			};
		}

		boolean test(@NotNull Context context);

		@Nullable
		default String getExpression() {
			return null;
		}

		@NotNull
		default Predicate or(@NotNull Predicate other) {
			Objects.requireNonNull(other, "other is null");
			return (context) -> this.test(context) || other.test(context);
		}

		@NotNull
		default Predicate or(@NotNull Iterable<Predicate> other) {
			Objects.requireNonNull(other, "other is null");
			return (context) -> {
				if (this.test(context))
					return true;

				for (Predicate predicate : other) {
					if (predicate.test(context))
						return true;
				}

				return false;
			};
		}

		@NotNull
		default Predicate or(@NotNull Predicate... other) {
			return this.or(List.of(other));
		}

		@NotNull
		default Predicate and(@NotNull Predicate other) {
			Objects.requireNonNull(other, "other is null");
			return (context) -> this.test(context) && other.test(context);
		}

		@NotNull
		default Predicate and(@NotNull Iterable<Predicate> other) {
			Objects.requireNonNull(other, "other is null");
			return (context) -> {
				if (!this.test(context))
					return false;

				for (Predicate predicate : other) {
					if (!predicate.test(context))
						return false;
				}

				return true;
			};
		}

		@NotNull
		default Predicate and(@NotNull Predicate... other) {
			return this.and(List.of(other));
		}
	}

	interface ElementValue {
		@NotNull
		static JsonPathOperation.ElementValue ofIndex(@NotNull Element element, int index) {
			Objects.requireNonNull(element, "element is null");

			if (index < 0)
				throw new IllegalArgumentException("Index cannot be negative");

			return new Index(element, index);
		}

		@NotNull
		static JsonPathOperation.ElementValue ofField(@NotNull Element element, @NotNull String field) {
			Objects.requireNonNull(element, "element is null");
			Objects.requireNonNull(field, "field is null");
			return new Field(element, field);
		}

		@NotNull
		Element getElement();

		@NotNull
		ElementValue ofElement(@NotNull Element element);

		final class Index implements ElementValue {
			private final Element element;
			private final int index;

			private Index(@NotNull Element element, int index) {
				Objects.requireNonNull(element, "element is null");
				this.element = element;
				this.index = index;
			}

			@NotNull
			@Override
			public Element getElement() {
				return this.element;
			}

			public int getIndex() {
				return this.index;
			}

			@NotNull
			@Override
			public ElementValue ofElement(@NotNull Element element) {
				Objects.requireNonNull(element, "element is null");
				return new Index(element, this.index);
			}

			@Override
			public String toString() {
				return "IndexValue[" + this.index + ", " + this.element + "]";
			}
		}

		final class Field implements ElementValue {
			private final Element element;
			private final String field;

			private Field(@NotNull Element element, @NotNull String field) {
				Objects.requireNonNull(element, "element is null");
				Objects.requireNonNull(field, "field is null");

				this.element = element;
				this.field = field;
			}

			@NotNull
			@Override
			public Element getElement() {
				return this.element;
			}

			@NotNull
			public String getField() {
				return this.field;
			}

			@NotNull
			@Override
			public ElementValue ofElement(@NotNull Element element) {
				Objects.requireNonNull(element, "element is null");
				return new Field(element, this.field);
			}

			@Override
			public String toString() {
				return "FieldValue[\"" + StringEscapeUtils.escapeJava(this.field) + "\", " + this.element + "]";
			}
		}
	}

	interface Context {
		@NotNull
		Element getRoot();

		@NotNull
		ElementValue getCurrent();
	}

	class Root implements JsonPathOperation {
		@NotNull
		@Override
		public List<ElementValue> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");
			return List.of(ElementValue.ofField(context.getRoot(), ""));
		}

		@Override
		public String toString() {
			return "$";
		}
	}

	class Self implements JsonPathOperation {
		@NotNull
		@Override
		public List<ElementValue> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");

			return List.of(context.getCurrent());
		}

		@Override
		public String toString() {
			return "@";
		}
	}

	class Wildcard implements JsonPathOperation {
		@NotNull
		@Override
		public List<ElementValue> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");

			Element element = context.getCurrent().getElement();

			if (element instanceof ArrayElement array)
				return IntStream
					.range(0, array.size())
					.mapToObj(i -> ElementValue.ofIndex(array.get(i), i))
					.toList();

			if (element instanceof ObjectElement object)
				return object
					.asMap()
					.entrySet()
					.stream()
					.map(entry -> ElementValue.ofField(entry.getValue(), entry.getKey()))
					.toList();

			return List.of();
		}

		@Override
		public String toString() {
			return "*";
		}
	}

	class Slice implements JsonPathOperation {
		private final int start;
		private final int end;
		private final int step;

		public Slice(int start, int end, int step) {
			if (step <= 0)
				throw new IllegalArgumentException("Step must be positive");

			this.start = start;
			this.end = end;
			this.step = step;
		}

		@NotNull
		@Override
		public List<ElementValue> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");

			Element element = context.getCurrent().getElement();
			if (!(element instanceof ArrayElement array))
				return List.of();

			int startInclusive = Math.max(0, this.start);
			int endExclusive = Math.min(this.end, array.size());

			if (this.step > 0)
				return IntStream
					.range(startInclusive, endExclusive)
					.filter(i -> (i - startInclusive) % this.step == 0)
					.mapToObj(i -> ElementValue.ofIndex(array.get(i), i))
					.toList();
			else
				return IntStream
					.rangeClosed(startInclusive, endExclusive)
					.filter(i -> (i - startInclusive) % this.step == 0)
					.mapToObj(i -> ElementValue.ofIndex(array.get(i), i))
					.toList();
		}

		@Override
		public String toString() {
			return "[" + StringUtils.joinNonNull(":",
				this.start == 0 ? "" : String.valueOf(this.start),
				this.end == Integer.MAX_VALUE ? "" : String.valueOf(this.end),
				this.step == 1 ? null : String.valueOf(this.step)) + "]";
		}
	}

	class Filter implements JsonPathOperation {
		private final Predicate predicate;

		public Filter(@NotNull Predicate predicate) {
			Objects.requireNonNull(predicate, "predicate is null");
			this.predicate = predicate;
		}

		@NotNull
		@Override
		public List<ElementValue> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");
			return this.predicate.test(context) ? List.of(context.getCurrent()) : List.of();
		}
	}

	class AccessField implements JsonPathOperation {
		private final Predicate predicate;

		public AccessField(@NotNull Predicate predicate) {
			Objects.requireNonNull(predicate, "predicate is null");
			this.predicate = predicate;
		}

		@NotNull
		@Override
		public List<ElementValue> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");

			Element element = context.getCurrent().getElement();
			Element root = context.getRoot();

			if (element instanceof ObjectElement object)
				return object
					.keys()
					.stream()
					.map(key -> ElementValue.ofField(object.get(key), key))
					.filter(value -> this.predicate.test(new SimpleJsonPathContext(root, value)))
					.toList();

			if (element instanceof ArrayElement array)
				return IntStream
					.range(0, array.size())
					.mapToObj(i -> ElementValue.ofIndex(array.get(i), i))
					.filter(value -> this.predicate.test(new SimpleJsonPathContext(root, value)))
					.toList();

			return List.of();
		}
	}

	class RecursiveAccessField implements JsonPathOperation {
		private final Predicate predicate;

		public RecursiveAccessField(@NotNull Predicate predicate) {
			Objects.requireNonNull(predicate, "predicate is null");
			this.predicate = predicate;
		}

		@NotNull
		@Override
		public List<ElementValue> apply(@NotNull Context context) {
			Objects.requireNonNull(context, "context is null");
			return this.recurse(context);
		}

		@NotNull
		protected List<ElementValue> recurse(@NotNull Context context) {
			Objects.requireNonNull(context, "elementValue is null");

			ElementValue current = context.getCurrent();
			Element element = current.getElement();

			List<ElementValue> results = new ArrayList<>();
			if (this.predicate.test(context))
				results.add(current);

			if (element instanceof ObjectElement object)
				results.addAll(object
					.keys()
					.stream()
					.map(key -> ElementValue.ofField(object.get(key), key))
					.map(value -> new SimpleJsonPathContext(context.getRoot(), value))
					.map(this::recurse)
					.flatMap(Collection::stream)
					.toList());

			if (element instanceof ArrayElement array)
				results.addAll(IntStream
					.range(0, array.size())
					.mapToObj(i -> ElementValue.ofIndex(array.get(i), i))
					.map(value -> new SimpleJsonPathContext(context.getRoot(), value))
					.map(this::recurse)
					.flatMap(Collection::stream)
					.toList());

			return results;
		}
	}
}
