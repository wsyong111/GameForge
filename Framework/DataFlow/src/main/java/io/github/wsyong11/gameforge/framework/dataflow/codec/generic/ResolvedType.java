package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Objects;

public interface ResolvedType extends Type {
	@NotNull
	default Type get(@NotNull TypeVariableToken token) {
		Objects.requireNonNull(token, "token is null");
		return this.get(token.getVariable());
	}

	@NotNull
	Type get(@NotNull TypeVariable<?> variable);

	@NotNull
	default Type getRaw(@NotNull TypeVariableToken token) {
		Objects.requireNonNull(token, "token is null");
		return this.get(token.getVariable());
	}

	@NotNull
	Type getRaw(@NotNull TypeVariable<?> variable);

	@NotNull
	Map<TypeVariable<?>, Type> getTypes();

	@NotNull
	Map<TypeVariable<?>, Type> getRawTypes();

	int count();

	@NotNull
	Class<?> getRawClass();

	@NotNull
	default ParameterizedType asParameterized() {
		return TypeUtils.parameterize(this.getRawClass(), this.getTypes());
	}
}
