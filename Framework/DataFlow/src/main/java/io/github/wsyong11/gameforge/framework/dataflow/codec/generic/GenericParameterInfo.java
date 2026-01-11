package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.TypeVariable;
import java.util.Objects;

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
		return itemProvider;
	}


}
