package io.github.wsyong11.gameforge.framework.dataflow.element;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ElementBuilder {
	@NotNull
	public static Element withObject(@Nullable Object obj) {
		if (obj == null)
			return Element.nil();

		if (obj instanceof Element element)
			return element;

		if (obj instanceof Map<?, ?> map) {
			Map<String, Element> result = new LinkedHashMap<>(map.size());
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Object keyObj = entry.getKey();
				if (!(keyObj instanceof String key))
					throw new IllegalArgumentException("Cannot build the element with an object, the key type is not a string: " + keyObj);

				result.put(key, withObject(entry.getValue()));
			}
			return Element.object(result);
		}

		if (obj instanceof List<?> list) {
			return Element.array(list
				.stream()
				.map(ElementBuilder::withObject)
				.toList());
		}

		if (obj instanceof String string)
			return Element.string(string);

		if (obj instanceof Boolean bool)
			return Element.primitive(bool);

		if (obj instanceof Number number)
			return Element.primitive(number);

		throw new UnsupportedOperationException("Cannot build the element with class " + obj.getClass().getName());
	}
}
