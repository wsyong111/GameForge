package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.TypeVariable;
import java.util.Objects;

public class GenericParameterInfo<T> {
	private final Class<T> type;
	private final int index;

	private final boolean ignore;
	private final TypeResolver<T> resolver;

	public GenericParameterInfo(@NotNull Class<T> type, int index) {
		this(type, index, true, null);
	}

	public GenericParameterInfo(@NotNull Class<T> type, int index, @NotNull TypeResolver<T> resolver) {
		this(type, index, false, Objects.requireNonNull(resolver, "resolver is null"));
	}

	protected GenericParameterInfo(@NotNull Class<T> type, int index, boolean ignore, @Nullable TypeResolver<T> resolver) {
		Objects.requireNonNull(type, "type is null");

		Objects.checkIndex(index, type.getTypeParameters().length);

		this.type = type;
		this.index = index;
		this.ignore = ignore;
		this.resolver = resolver;

		assert (resolver == null && ignore)
			|| (resolver != null && !ignore)
			: "The resolver and ignore statuses are inconsistent";
	}

	@NotNull
	public Class<T> getType() {
		return this.type;
	}

	public int getIndex() {
		return this.index;
	}

	public boolean isIgnore() {
		return this.ignore;
	}

	@Nullable
	public TypeResolver<T> getResolver() {
		return this.resolver;
	}

	@Override
	public String toString() {
		TypeVariable<Class<T>>[] typeParameters = this.type.getTypeParameters();
		return "Parameter("
			+ this.type + ", "
			+ typeParameters[this.index]
			+ (this.ignore ? ", ignore" : "")
			+ ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GenericParameterInfo<?> that = (GenericParameterInfo<?>) o;
		return this.index == that.index
			&& this.ignore == that.ignore
			&& Objects.equals(this.type, that.type)
			&& Objects.equals(this.resolver, that.resolver);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.index, this.ignore, this.resolver);
	}

	public static class Builder<V> {
		private final Class<V> type;
		private final int index;

		private TypeResolver<V> resolver;
		private boolean ignored;

		public Builder(@NotNull Class<V> type, int index) {
			Objects.requireNonNull(type, "type is null");
			Objects.checkIndex(index, type.getTypeParameters().length);

			this.type = type;
			this.index = index;

			this.resolver = null;
			this.ignored = false;
		}

		@NotNull
		public Builder<V> resolver(@NotNull TypeResolver<V> resolver) {
			Objects.requireNonNull(resolver, "resolver is null");
			this.resolver = resolver;
			this.ignored = false;
			return this;
		}

		@NotNull
		public Builder<V> ignore() {
			this.resolver = null;
			this.ignored = true;
			return this;
		}

		@NotNull
		public GenericParameterInfo<V> build() {
			if (this.ignored)
				return new GenericParameterInfo<>(this.type, this.index);

			Objects.requireNonNull(this.resolver, "Require ignore");
			return new GenericParameterInfo<>(this.type, this.index, this.resolver);
		}
	}
}
