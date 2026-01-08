package io.github.wsyong11.gameforge.framework.dataflow.codec;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.info.GenericHandlerInfo;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.tree.GenericItem;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class SimpleCodecs extends AbstractCodecs {
	@NotNull
	protected GenericItem resolve(@NotNull Type type, @NotNull Object value) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(value, "value is null");

		Class<?> valueType = value.getClass();

		if (type instanceof ParameterizedType parameterizedType) {
			Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			if (rawType != valueType)
				throw new IllegalArgumentException("Type mismatch, type " + rawType + " != " + valueType);

			GenericHandlerInfo<?> handlerInfo = this.findGenericHandler(rawType);
			if (handlerInfo==null){

			}
		}
	}

	@NotNull
	@Override
	public <T> Element encode(@Nullable T value, @NotNull Class<T> type) throws CodecException {
		Objects.requireNonNull(type, "type is null");

		CodecContext ctx = new CodecContextImpl(type);
		return this.encodeWithCodec(ctx, value, type);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	public <T> Element encode(@Nullable T value, @NotNull TypeToken<T> type) throws CodecException {
		Objects.requireNonNull(type, "type is null");

		if (value==null)
			return Element.nil();

		GenericItem resolved = this.resolve(type.getType(), value);
		System.out.println(resolved);
//		Type rawType = type.getType();
//		if (rawType instanceof TypeVariable<?> typeVariable)
//			throw new IllegalArgumentException("Cannot resolve type parameter " + typeVariable.getName());
//
//		if (rawType instanceof Class<?> classType) {
//			TypeVariable<? extends Class<?>>[] typeParameters = classType.getTypeParameters();
//			if (typeParameters.length == 0)
//				return this.encode(value, (Class<? super Object>) classType);
//
//			throw new UnsupportedOperationException();
//		}
//
//		if (rawType instanceof ParameterizedType parameterized){
//			Class<?> parameterizedType = (Class<?>) parameterized.getRawType();
//
//			for (Type argument : parameterized.getActualTypeArguments()) {
//
//			}
//		}

		return null;
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

		@Override
		public @NotNull <T> Element encode(@Nullable T value, @NotNull Class<T> type) throws CodecException {
			Objects.requireNonNull(type, "type is null");
			return SimpleCodecs.this.encodeWithCodec(this, value, type);
		}

		@Nullable
		@Override
		public <T> T decode(@NotNull Element element, @NotNull Class<T> type) throws CodecException {
			Objects.requireNonNull(element, "element is null");
			Objects.requireNonNull(type, "type is null");
			return SimpleCodecs.this.decodeWithCodec(this, element, type);
		}

		@NotNull
		@Override
		public Type getRootType() {
			return this.rootType;
		}
	}
}
