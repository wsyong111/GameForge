package io.github.wsyong11.gameforge.framework.dataflow.codec;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.framework.dataflow.codec.ex.GenericCodecException;
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
import java.util.stream.Collectors;

public class SimpleCodecs extends AbstractCodecs {

	@NotNull
	@Override
	public <T> Element encode(@Nullable T value, @NotNull Class<T> type) throws CodecException {
		Objects.requireNonNull(type, "type is null");

		CodecContext ctx = new CodecContextImpl(type);
		return this.encodeWithCodec(ctx, value, type);
	}

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

		if (value == null)
			return Element.nil();



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
