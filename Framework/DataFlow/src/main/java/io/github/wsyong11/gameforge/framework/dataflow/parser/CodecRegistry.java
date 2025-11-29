package io.github.wsyong11.gameforge.framework.dataflow.parser;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.ex.CodecNotFoundException;
import io.github.wsyong11.gameforge.framework.dataflow.ex.ElementCodecException;
import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CodecRegistry {
	private static final Map<String, Lazy<ElementCodec>> CODEC = new ConcurrentHashMap<>();

	static {
		register(JsonElementCodec::new, "json", "json5");
	}

	private static void register(@NotNull Supplier<ElementCodec> codec, @NotNull String... names) {
		Objects.requireNonNull(codec, "codec is null");
		Objects.requireNonNull(names, "names is null");

		for (String name : names)
			CODEC.put(name, Lazy.concurrentOf(codec));
	}

	@NotNull
	private static ElementCodec getCodec(@NotNull String name) throws CodecNotFoundException {
		Objects.requireNonNull(name, "name is null");

		Lazy<ElementCodec> codec = CODEC.get(name);
		if (codec == null)
			throw new CodecNotFoundException(name);

		return codec.get();
	}

	@NotNull
	public Element decode(@NotNull String text, @NotNull String typeName) throws ElementCodecException {
		Objects.requireNonNull(text, "text is null");
		Objects.requireNonNull(typeName, "typeName is null");
		return getCodec(typeName).decode(text);
	}

	@NotNull
	public String encode(@NotNull Element element, @NotNull String typeName) throws ElementCodecException {
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(typeName, "typeName is null");
		return getCodec(typeName).encode(element);
	}
}
