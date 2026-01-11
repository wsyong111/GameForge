package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.framework.dataflow.element.ArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.MissingElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.ObjectElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class GenericInfos {
	public static final GenericInfo<Map<?, ?>> MAP = GenericInfo
		.builder(new TypeToken<Map<?, ?>>() {})
		.encoder((ctx, map, types) -> {
			Type keyType = types.get(0);
			Type valueType = types.get(1);

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
		})
		.decoder((ctx, element, types) -> {
			if (!(element instanceof ArrayElement array))
				throw new CodecException();

			Type keyType = types.get(0);
			Type valueType = types.get(1);

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
		})
		.parameter(0, b -> b
			.itemProvider(Map::keySet))
		.parameter(1, b -> b
			.itemProvider(Map::values))
		.build();
}

/*
struct GenericInfo<T> {
	GenericCodec<T> codec;
	ParameterInfo<T>[] parameters;
}

struct ParameterInfo<T> {
	TypeVariable<T> variable;
	ItemProvider<T> itemProvider;
	TypeVariableToken token;
}

struct TypeVariableToken {
	TypeVariable<?> variable;
}

interface ItemProvider<T> {
	Collection<?> get(T value);
}
 */