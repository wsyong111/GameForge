package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResolvedTypeImpl implements ResolvedType {
	@NotNull
	public static Pair<Type, Type> item(@NotNull Type rawType, @NotNull Type resolvedType) {
		Objects.requireNonNull(rawType, "rawType is null");
		Objects.requireNonNull(resolvedType, "resolvedType is null");
		return Pair.ofNonNull(rawType, resolvedType);
	}

	@NotNull
	public static Type itemRawType(@NotNull Pair<Type, Type> pair) {
		Objects.requireNonNull(pair, "pair is null");
		return pair.getLeft();
	}

	@NotNull
	public static Type itemResolvedType(@NotNull Pair<Type, Type> pair) {
		Objects.requireNonNull(pair, "pair is null");
		return pair.getRight();
	}

	private final Class<?> rawType;
	private final Map<TypeVariable<?>, Pair<Type, Type>> typeMap;

	public ResolvedTypeImpl(@NotNull Class<?> rawType, @NotNull Map<TypeVariable<?>, Pair<Type, Type>> typeMap) {
		Objects.requireNonNull(rawType, "rawType is null");
		Objects.requireNonNull(typeMap, "typeMap is null");

		this.rawType = rawType;
		this.typeMap = Map.copyOf(typeMap);

		assert this.assertCheck();
	}

	private boolean assertCheck() {
		TypeVariable<?>[] parameters = this.rawType.getTypeParameters();

		for (TypeVariable<?> parameter : parameters)
			assert this.typeMap.containsKey(parameter) : "Missing key " + parameter.getName();

		return true;
	}

	@NotNull
	private Pair<Type, Type> getType(@NotNull TypeVariable<?> variable) {
		Objects.requireNonNull(variable, "variable is null");

		Pair<Type, Type> pair = this.typeMap.get(variable);
		if (pair == null)
			throw new IllegalArgumentException("Unknown type variable " + variable.getName());
		return pair;
	}

	@NotNull
	@Override
	public Type get(@NotNull TypeVariable<?> variable) {
		Objects.requireNonNull(variable, "variable is null");
		return itemResolvedType(this.getType(variable));
	}

	@NotNull
	@Override
	public Type getRaw(@NotNull TypeVariable<?> variable) {
		Objects.requireNonNull(variable, "variable is null");
		return itemRawType(this.getType(variable));
	}

	@NotNull
	@Override
	public Map<TypeVariable<?>, Type> getTypes() {
		return this.typeMap
			.entrySet()
			.stream()
			.collect(Collectors.toUnmodifiableMap(
				Map.Entry::getKey,
				e -> itemResolvedType(e.getValue())
			));
	}

	@NotNull
	@Override
	public Map<TypeVariable<?>, Type> getRawTypes() {
		return this.typeMap
			.entrySet()
			.stream()
			.collect(Collectors.toUnmodifiableMap(
				Map.Entry::getKey,
				e -> itemRawType(e.getValue())
			));
	}

	@Override
	public int count() {
		return this.typeMap.size();
	}

	@NotNull
	@Override
	public Class<?> getRawClass() {
		return this.rawType;
	}

	@Override
	public String toString() {
		return this.rawType.getName() + "<" + Arrays
			.stream(this.rawType.getTypeParameters())
			.map(this.typeMap::get)
			.map(ResolvedTypeImpl::itemResolvedType)
			.map(Type::getTypeName)
			.collect(Collectors.joining(", ")) + ">";
	}
}
