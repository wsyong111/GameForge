package io.github.wsyong11.gameforge.framework.dataflow.codec.generic.codec;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericCodec;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericInfo;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.ResolvedType;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.TypeVariableToken;
import io.github.wsyong11.gameforge.framework.dataflow.element.ArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.MissingElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.ObjectElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MapCodec implements GenericCodec<Map<?, ?>> {
	private static final TypeVariableToken K = TypeVariableToken.of(Map.class, 0);
	private static final TypeVariableToken V = TypeVariableToken.of(Map.class, 1);

	@NotNull
	@Override
	public Class<? super Map<?, ?>> getType() {
		return Map.class;
	}

	@Override
	public void buildInfo(@NotNull GenericInfo.Builder<Map<?, ?>> builder) {
		Objects.requireNonNull(builder, "builder is null");

		builder.parameter(K).itemProvider(Map::keySet);
		builder.parameter(V).itemProvider(Map::values);
	}

	@NotNull
	@Override
	public Map<?, ?> decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull ResolvedType types) throws CodecException {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(types, "types is null");

		if (!(element instanceof ArrayElement array))
			throw new CodecException();

		Type keyType = types.get(K);
		Type valueType = types.get(V);

		Map<Object, Object> map = new LinkedHashMap<>();
		for (Element item : array.asList()) {
			if (!(item instanceof ObjectElement obj))
				throw new CodecException();

			Element keyElement = obj.get("key");
			if (keyElement instanceof MissingElement)
				throw new CodecException();

			Element valueElement = obj.get("value");
			if (valueElement instanceof MissingElement)
				throw new CodecException();

			Object key = ctx.decode(keyElement, keyType);
			Object value = ctx.decode(valueElement, valueType);
			map.put(key, value);
		}

		return map;
	}

	@NotNull
	@Override
	public Element encode(@NotNull CodecContext ctx, @NotNull Map<?, ?> map, @NotNull ResolvedType types) throws CodecException {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(map, "map is null");
		Objects.requireNonNull(types, "types is null");

		Type keyType = types.get(K);
		Type valueType = types.get(V);

		MutableArrayElement element = MutableElement.array();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Element keyElement = ctx.encode(entry.getKey(), keyType);
			Element valueElement = ctx.encode(entry.getValue(), valueType);

			element.add(MutableElement
				.object()
				.add("key", keyElement.asMutable())
				.add("value", valueElement.asMutable()));
		}
		return element.asElement();
	}

	@Override
	public boolean isSupportElement(@NotNull CodecContext ctx, @NotNull Element element) {
		return element instanceof ArrayElement;
	}
}
