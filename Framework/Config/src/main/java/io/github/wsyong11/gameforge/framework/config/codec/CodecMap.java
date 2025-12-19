package io.github.wsyong11.gameforge.framework.config.codec;

import io.github.wsyong11.gameforge.framework.config.ex.RuntimeCodecException;
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
import java.util.stream.Collectors;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class CodecMap {
	private static final Logger LOGGER = Log.getLogger();

	private final Object lock;

	private final Set<ValueCodec<?>> codecs;
	private final Map<ValueCodec<?>, Set<Class<?>>> codecSupportTypeMap;
	private final Map<Class<?>, List<ValueCodec<?>>> codecTypeMap;

	private volatile Map<Class<?>, List<ValueCodec<?>>> codecTypeMapSnapshot;

	public CodecMap(){
		this(true);
	}

	public CodecMap(boolean defaultCodec) {
		this.lock = new Object();
		this.codecs = new LinkedHashSet<>();
		this.codecSupportTypeMap = new HashMap<>();
		this.codecTypeMap = new WeakHashMap<>();

		this.codecTypeMapSnapshot = Map.of();

		if(defaultCodec)
			ValueCodecs.fill(this);
	}

	@UnmodifiableView
	public <T> List<ValueCodec<T>> getCodec(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		return CollectionUtils.forceCast(this.codecTypeMapSnapshot.getOrDefault(type, List.of()));
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
				if (!codec.isSupportedElement(element))
					continue;

				return codec.decode(element, type);
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
				if (!codec.isSupportedValue(value))
					continue;

				return codec.encode(value, type);
			} catch (Exception e) {
				exceptionHandler.accept(e);
				LOGGER.trace("Encoder exception {}", lazy(codec), e);
			}
		}

		throw exceptionHandler.toException("No suitable encoder found for a type " + type,
			RuntimeCodecException::new);
	}

	private void updateTypeSnapshot() {
		this.codecTypeMapSnapshot = this.codecTypeMap
			.entrySet()
			.stream()
			.collect(Collectors.toUnmodifiableMap(
				Map.Entry::getKey,
				e -> List.copyOf(e.getValue())
			));
	}

	@SuppressWarnings("unchecked")
	public boolean add(@NotNull ValueCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		synchronized (this.lock) {
			if (!this.codecs.add(codec))
				return false;

			Set<Class<?>> supportTypes = (Set<Class<?>>) codec.getSupportTypes();
			this.codecSupportTypeMap.put(codec, supportTypes);
			for (Class<?> type : supportTypes) {
				this.codecTypeMap
					.computeIfAbsent(type, k -> new ArrayList<>())
					.add(codec);
			}

			this.updateTypeSnapshot();
			return true;
		}
	}

	public boolean remove(@NotNull ValueCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		synchronized (this.lock) {
			if (!this.codecs.remove(codec))
				return false;

			Set<Class<?>> supportTypes = this.codecSupportTypeMap.remove(codec);
			for (Class<?> type : supportTypes) {
				this.codecTypeMap
					.getOrDefault(type, List.of())
					.remove(codec);
			}

			this.updateTypeSnapshot();
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
}
