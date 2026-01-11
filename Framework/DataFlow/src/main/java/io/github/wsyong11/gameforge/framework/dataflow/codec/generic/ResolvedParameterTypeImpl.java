package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import io.github.wsyong11.gameforge.framework.annotation.Internal;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

@Internal
public class ResolvedParameterTypeImpl implements ResolvedParameterType {
	@NotNull
	@Override
	public Type get(int index) {
		return null;Class.class.getTypeParameters()
	}

	@Override
	public int count() {
		return 0;
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<Type> getTypes() {
		return List.of();
	}

	@Override
	public @NotNull Class<?> getRawType() {
		return null;
	}

	@NotNull
	@Override
	public ParameterizedType asParameterized() {

	}

	@Override
	public String toString() {

	}
}
