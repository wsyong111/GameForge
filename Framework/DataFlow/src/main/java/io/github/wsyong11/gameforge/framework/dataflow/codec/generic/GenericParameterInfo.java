package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.TypeVariable;
import java.util.Objects;
import java.util.function.Function;

public class GenericParameterInfo<T> {
	private final TypeVariable<Class<T>> variable;

	@Nullable
	private final ItemProvider<T> itemProvider;

	public GenericParameterInfo(@NotNull TypeVariable<Class<T>> variable, @Nullable ItemProvider<T> itemProvider) {
		Objects.requireNonNull(variable, "variable is null");

		this.variable = variable;
		this.itemProvider = itemProvider;
	}

	@NotNull
	public TypeVariable<Class<T>> getVariable() {
		return this.variable;
	}

	@Nullable
	public ItemProvider<T> getItemProvider() {
		return this.itemProvider;
	}

	public static class Builder<V> {
		private final TypeVariable<Class<V>> variable;

		@Nullable
		private ItemProvider<V> itemProvider;

		public Builder(@NotNull TypeVariable<Class<V>> variable) {
			Objects.requireNonNull(variable, "variable is null");
			this.variable = variable;
		}

		@NotNull
		public Builder<V> itemProvider(@Nullable ItemProvider<V> provider) {
			this.itemProvider = provider;
			return this;
		}

		@NotNull
		public GenericParameterInfo<V> build() {
			return new GenericParameterInfo<>(this.variable, this.itemProvider);
		}
	}
}
