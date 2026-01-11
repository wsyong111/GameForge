package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.util.reflect.ReflectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GenericInfo<T> {
	private final Class<T> type;
	private final Map<TypeVariable<Class<T>>, GenericParameterInfo<T>> parameters;
	private final GenericCodec<T> codec;

	public GenericInfo(
		@NotNull Class<T> type,
		@NotNull Map<TypeVariable<Class<T>>, GenericParameterInfo<T>> parameters,
		@NotNull GenericCodec<T> codec
	) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(parameters, "parameters is null");
		Objects.requireNonNull(codec, "codec is null");

		this.type = type;
		this.parameters = Map.copyOf(parameters);
		this.codec=codec;

		Set<TypeVariable<Class<T>>> typeParameters = Set.of(type.getTypeParameters());

		for (Map.Entry<TypeVariable<Class<T>>, GenericParameterInfo<T>> entry : this.parameters.entrySet()) {
			TypeVariable<Class<T>> key = entry.getKey();
			GenericParameterInfo<T> value = entry.getValue();

			if (!typeParameters.contains(key))
				throw new IllegalArgumentException("Type variable %s is not declared by %s (declared by %s)".formatted(
					key.getName(),
					type.getName(),
					ReflectUtils.declarationToString(key.getGenericDeclaration())));

			if (!Objects.equals(key, value.getVariable()))
				throw new IllegalArgumentException("Type variable %s is not declared by %s (declared by %s)".formatted(
					key.getName(),
					type.getName(),
					ReflectUtils.declarationToString(key.getGenericDeclaration())));
		}
	}

	@NotNull
	public Class<T> getType() {
		return this.type;
	}

	@NotNull
	@UnmodifiableView
	public Map<TypeVariable<Class<T>>, GenericParameterInfo<T>> getParameters() {
		return this.parameters;
	}

	@Nullable
	public GenericParameterInfo<T> getParameter(@NotNull TypeVariable<?> variable){
		Objects.requireNonNull(variable, "variable is null");
		return this.parameters.get(variable);
	}

	@Nullable
	public GenericParameterInfo<T> getParameter(@NotNull TypeVariableToken token){
		Objects.requireNonNull(token, "token is null");
		return this.getParameter(token.getVariable());
	}

	@NotNull
	public GenericCodec<T> getCodec() {
		return this.codec;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static class Builder<V> {
		private final Class<V> type;

		public Builder(@NotNull Class<V> type) {
			Objects.requireNonNull(type, "type is null");
			this.type = type;
		}

		@NotNull
		public GenericInfo<V> build() {

		}
	}
}
