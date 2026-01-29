package io.github.wsyong11.gameforge.framework.dataflow.codec;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.framework.dataflow.codec.ex.GenericCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.codec.ex.GenericCodecNotFoundException;
import io.github.wsyong11.gameforge.framework.dataflow.codec.ex.GenericTypeResolveException;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.*;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleCodecs extends AbstractCodecs {
	@NotNull
	@Override
	public <T> Element encode(@Nullable T value, @NotNull TypeToken<T> type) throws CodecException {
		Objects.requireNonNull(type, "type is null");

		Type genericType = type.getType();
		CodecContext ctx = new CodecContextImpl(genericType);
		return this.encodeGeneric(ctx, value, genericType);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private Element encodeGeneric(@NotNull CodecContext ctx, @Nullable Object value, @NotNull Type type) throws CodecException {
		Objects.requireNonNull(type, "type is null");

		System.out.println(type);

		if (value == null)
			return Element.nil();

		Type resolveType = this.resolveType(type, value);

		throw new GenericCodecException("Unsupported generic type " + type);
	}

	@NotNull
	private List<Type> getTypeParameters(@NotNull Type type) {
		Objects.requireNonNull(type, "type is null");

		if (type instanceof Class<?> classType)
			return Collections.nCopies(
				classType.getTypeParameters().length,
				TypeUtils.WILDCARD_ALL);

		if (type instanceof ParameterizedType parameterizedType)
			return List.of(parameterizedType.getActualTypeArguments());

		throw new IllegalArgumentException("Unsupported type " + type);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private Type resolveType(@NotNull Type type, @NotNull Object value) throws GenericCodecNotFoundException, GenericTypeResolveException {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(value, "value is null");

//		Class<?> rawType;
		Map<TypeVariable<?>, Type> rawTypeParameters;
		GenericInfo<?> info;

		if (type instanceof Class<?> classType) {
			List<GenericInfo<?>> genericInfos = (List<GenericInfo<?>>) this.findGenericInfo(classType);
			if (genericInfos.isEmpty())
				return classType;

			info = genericInfos.get(0);

			rawTypeParameters = Arrays
				.stream(classType.getTypeParameters())
				.collect(Collectors.toUnmodifiableMap(
					Function.identity(),
					v -> TypeUtils.WILDCARD_ALL
				));
		} else if (type instanceof ParameterizedType parameterizedType) {
			Class<?> rawType = (Class<?>) parameterizedType.getRawType();

			List<GenericInfo<?>> genericInfos = (List<GenericInfo<?>>) this.findGenericInfo(rawType);
			if (genericInfos.isEmpty())
				throw new GenericCodecNotFoundException("Cannot find generic codec from type " + parameterizedType);

			info = genericInfos.get(0);

			TypeVariable<? extends Class<?>>[] typeParameters = rawType.getTypeParameters();
			Type[] typeArguments = parameterizedType.getActualTypeArguments();

			rawTypeParameters = IntStream
				.range(0, typeParameters.length)
				.boxed()
				.collect(Collectors.toUnmodifiableMap(
					i -> typeParameters[i],
					i -> typeArguments[i]
				));
		} else {
			return type;
		}

		GenericInfo<Object> unsafeInfo = (GenericInfo<Object>) info;

		Map<TypeVariable<?>, Pair<Type, Type>> resolvedTypeMap = new HashMap<>();
		for (Map.Entry<TypeVariable<?>, Type> entry : rawTypeParameters.entrySet()) {
			TypeVariable<?> key = entry.getKey();
			Type rawTypeValue = entry.getValue();

			GenericParameterInfo<Object> parameterInfo = unsafeInfo.getParameter(key);
			if (parameterInfo == null) {
				resolvedTypeMap.put(key, ResolvedTypeImpl.item(rawTypeValue, rawTypeValue));
				continue;
			}

			ItemProvider<Object> itemProvider = parameterInfo.getItemProvider();
			if (itemProvider == null) {
				if (!(rawTypeValue instanceof ParameterizedType) && !(rawTypeValue instanceof Class<?>))
					throw new GenericTypeResolveException("Cannot resolve generic type " + rawTypeValue + ". Full type " + type);

				resolvedTypeMap.put(key, ResolvedTypeImpl.item(rawTypeValue, rawTypeValue));
				continue;
			}

			List<?> items;			try {
				// Don't use List.copyOf, we need support null item
				items = new ArrayList<>(itemProvider.get(value));
			} catch (Exception e) {
				throw new GenericTypeResolveException("Cannot resolve generic type " + rawTypeValue + ". Full type " + type, e);
			}

			List<Class<?>> itemTypes = items
				.stream()
				.filter(Objects::nonNull)
				.<Class<?>>map(Object::getClass)
				.toList();

			// TODO: 2026/01/25 Find support type
		}

		Class<?> rawType = info.getType();
		return new ResolvedTypeImpl(rawType, resolvedTypeMap);
	}

	@Nullable
	@Override
	public <T> T decode(@NotNull Element element, @NotNull Class<T> type) throws CodecException {
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(type, "type is null");

		CodecContext ctx = new CodecContextImpl(type);
		return this.decodeWithCodec(ctx, element, type);
	}

	@Nullable
	@Override
	public <T> T decode(@NotNull Element element, @NotNull TypeToken<T> type) {
		return null;
	}

	private class CodecContextImpl implements CodecContext {
		private final Type rootType;

		private CodecContextImpl(@NotNull Type rootType) {
			Objects.requireNonNull(rootType, "rootType is null");
			this.rootType = rootType;
		}

		@NotNull
		@Override
		public Element encode(@Nullable Object value, @NotNull Type type) throws CodecException {
			Objects.requireNonNull(type, "type is null");
			return SimpleCodecs.this.encodeGeneric(this, value, type);
		}

		@Nullable
		@Override
		public Object decode(@NotNull Element element, @NotNull Type type) throws CodecException {
			Objects.requireNonNull(element, "element is null");
			Objects.requireNonNull(type, "type is null");
			throw new UnsupportedOperationException();
//			return SimpleCodecs.this.decodeWithCodec(this, element, type);
		}

		@NotNull
		@Override
		public Type getRootType() {
			return this.rootType;
		}
	}
}
