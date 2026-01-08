package io.github.wsyong11.gameforge.framework.config.codec;

import io.github.wsyong11.gameforge.framework.config.ex.RuntimeCodecException;
import io.github.wsyong11.gameforge.framework.config.ex.ValueCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.NullElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.collection.CollectionUtils;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class CodecMap {
	private static final Logger LOGGER = Log.getLogger();

	private final Object lock;

	private final Set<ValueCodec<?>> codecs;
	private final Map<Class<?>, List<ValueCodec<?>>> codecTypeMap;
	private final CodecContext context;

	public CodecMap() {
		this(true);
	}

	public CodecMap(boolean defaultCodec) {
		this.lock = new Object();
		this.codecs = new LinkedHashSet<>();
		this.codecTypeMap = new ConcurrentHashMap<>();

		this.context = new ContextImpl();

		if (defaultCodec)
			ValueCodecs.fill(this);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@NotNull
	private List<ValueCodec<?>> loadCodec(@NotNull Class<?> type) {
		Objects.requireNonNull(type, "type is null");

		List<ValueCodec<?>> result = new ArrayList<>();
		for (ValueCodec<?> codec : this.codecs) {
			try {
				if (codec.isSupportType((Class) type))
					result.add(codec);
			} catch (Exception e) {
				LOGGER.warn("Uncaught exception with invoke {}", lazy(codec), e);
			}
		}
		return List.copyOf(result);
	}

	@UnmodifiableView
	public <T> List<ValueCodec<T>> getCodec(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		return CollectionUtils.forceCast(this.codecTypeMap.computeIfAbsent(type, this::loadCodec));
	}

	@Nullable
	public <T> T decode(@NotNull Element element, @NotNull Class<T> type) throws RuntimeCodecException {
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(type, "type is null");

		if (element instanceof NullElement)
			return null;

		try {
			if (Element.class.isAssignableFrom(type)) {
				return type.cast(element);
			} else if (MutableElement.class.isAssignableFrom(type)) {
				return type.cast(element.asMutable());
			}
		} catch (ClassCastException e) {
			throw new RuntimeCodecException("Failed to cast " + element + " to " + type, e);
		}

		ExceptionHandler exceptionHandler = new ExceptionHandler();

		for (ValueCodec<T> codec : this.getCodec(type)) {
			try {
				if (!codec.isSupportElement(type, element))
					continue;

				return codec.decode(this.context, element, type);
			} catch (Exception e) {
				exceptionHandler.accept(e);
				LOGGER.trace("Decoder exception {}", lazy(codec), e);
			}
		}

		throw exceptionHandler.toException("No suitable decoder found for a type " + type,
			RuntimeCodecException::new);
	}

	@NotNull
	public <T> Element encode(@Nullable T value, @NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		if (value == null)
			return Element.nil();

		if (MutableElement.class.isAssignableFrom(type) ||
			Element.class.isAssignableFrom(type)
		) {
			if (value instanceof Element element)
				return element;

			if (value instanceof MutableElement element)
				return MutableElement.asElementSafe(element);

			throw new RuntimeCodecException("Failed to cast " + value + " to " + type);
		}

		ExceptionHandler exceptionHandler = new ExceptionHandler();
		for (ValueCodec<T> codec : this.getCodec(type)) {
			try {
				if (!codec.isSupportValue(type, value))
					continue;

				return codec.encode(this.context, value, type);
			} catch (Exception e) {
				exceptionHandler.accept(e);
				LOGGER.trace("Encoder exception {}", lazy(codec), e);
			}
		}

		throw exceptionHandler.toException("No suitable encoder found for a type " + type,
			RuntimeCodecException::new);
	}

	public boolean add(@NotNull ValueCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		synchronized (this.lock) {
			if (!this.codecs.add(codec))
				return false;

			this.codecTypeMap.clear();
			return true;
		}
	}

	public boolean remove(@NotNull ValueCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		synchronized (this.lock) {
			if (!this.codecs.remove(codec))
				return false;

			this.codecTypeMap.entrySet().removeIf(e -> {
				List<ValueCodec<?>> codecList = e.getValue();
				codecList.removeIf(v -> Objects.equals(v, codec));
				return codecList.isEmpty();
			});
			return true;
		}
	}

	@NotNull
	@Unmodifiable
	public List<ValueCodec<?>> getCodecs() {
		synchronized (this.lock) {
			return List.copyOf(this.codecs);
		}
	}

	private class ContextImpl implements CodecContext {
		@NotNull
		@Override
		public <T> Element encode(@NotNull T value, @NotNull Class<T> type) throws ValueCodecException {
			try {
				return CodecMap.this.encode(value, type);
			} catch (RuntimeCodecException e) {
				throw new ValueCodecException(e);
			}
		}

		@Nullable
		@Override
		public <T> T decode(@NotNull Element element, @NotNull Class<T> type) throws ValueCodecException {
			try {
				return CodecMap.this.decode(element, type);
			} catch (RuntimeCodecException e) {
				throw new ValueCodecException(e);
			}
		}
	}
}
