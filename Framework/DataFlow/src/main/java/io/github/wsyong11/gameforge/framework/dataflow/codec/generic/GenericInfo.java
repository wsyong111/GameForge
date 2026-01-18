package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import io.github.wsyong11.gameforge.util.reflect.ReflectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Consumer;

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
		this.codec = codec;

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
	public GenericParameterInfo<T> getParameter(@NotNull TypeVariable<?> variable) {
		Objects.requireNonNull(variable, "variable is null");
		return this.parameters.get(variable);
	}

	@Nullable
	public GenericParameterInfo<T> getParameter(@NotNull TypeVariableToken token) {
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
		private final GenericCodec<V> codec;

		private final Set<TypeVariable<Class<V>>> typeVariables;
		private final Map<TypeVariable<Class<V>>, GenericParameterInfo.Builder<V>> parameters;

		public Builder(@NotNull Class<V> type, @NotNull GenericCodec<V> codec) {
			Objects.requireNonNull(type, "type is null");
			Objects.requireNonNull(codec, "codec is null");

			this.type = type;
			this.codec = codec;

			this.typeVariables = Set.of(type.getTypeParameters());
			this.parameters = new HashMap<>();
		}

		@SuppressWarnings("unchecked")
		@NotNull
		public GenericParameterInfo.Builder<V> parameter(@NotNull TypeVariableToken token) {
			Objects.requireNonNull(token, "token is null");

			TypeVariable<?> variable = token.getVariable();
			if (!this.typeVariables.contains(variable))
				throw new IllegalArgumentException("Type variable %s is not declared by %s".formatted(
					variable.getName(),
					this.type.getName()));

			TypeVariable<Class<V>> typeVariable = (TypeVariable<Class<V>>) variable;

			GenericParameterInfo.Builder<V> builder = new GenericParameterInfo.Builder<>(typeVariable);
			this.parameters.put(typeVariable, builder);
			return builder;
		}

		@NotNull
		public Builder<V> parameter(@NotNull TypeVariableToken token, @NotNull Consumer<GenericParameterInfo.Builder<V>> callback) {
			Objects.requireNonNull(token, "token is null");
			callback.accept(this.parameter(token));
			return this;
		}

		@NotNull
		public GenericInfo<V> build() {
			Map<TypeVariable<Class<V>>, GenericParameterInfo<V>> parameters = new HashMap<>();
			this.parameters.forEach((key, value) ->
				parameters.put(key, value.build()));

			return new GenericInfo<>(this.type, parameters, this.codec);
		}
	}
}
