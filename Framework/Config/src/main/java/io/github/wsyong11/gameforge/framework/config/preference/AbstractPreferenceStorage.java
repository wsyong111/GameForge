package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.config.preference.ex.PreferencesCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.NullElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public abstract class AbstractPreferenceStorage implements PreferenceStorage {
	private static final Logger LOGGER = Log.getLogger();

	protected static final List<ValueCodec<?>> DEFAULT_CODECS = List.of(
		ValueCodecs.STRING_CODEC,
		ValueCodecs.NUMBER_CODEC,
		ValueCodecs.BOOLEAN_CODEC
	);

	// TODO: 2025/12/10 优化Codec查找速度
	private final ReadWriteLock codecsLock;
	private final Set<ValueCodec<?>> codecs;
	private final Map<Class<?>, List<ValueCodec<?>>> codecTypeCache;

	public AbstractPreferenceStorage() {
		this.codecsLock = new ReentrantReadWriteLock();
		this.codecs = new LinkedHashSet<>();
		this.codecTypeCache = new ConcurrentHashMap<>();

		DEFAULT_CODECS.forEach(this::registerCodec);
	}

	@NotNull
	private List<ValueCodec<?>> createCodecCache(@NotNull Class<?> key) {
		Objects.requireNonNull(key, "key is null");

		return this
			.getCodecs()
			.stream()
			.filter(codec -> codec.getSupportTypes().contains(key))
			.toList();
	}

	@SuppressWarnings("unchecked")
	@Nullable
	protected <T> T decode(@NotNull Element element, @NotNull Class<T> type) throws PreferencesCodecException {
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
			throw new PreferencesCodecException("Failed to cast " + element + " to " + type, e);
		}

		List<ValueCodec<?>> codecs = this.codecTypeCache.computeIfAbsent(type, this::createCodecCache);

		ExceptionHandler exceptionHandler = new ExceptionHandler();
		for (ValueCodec<?> codec : codecs) {
			try {
				if (!codec.isSupportedElement(element))
					continue;

				ValueCodec<T> realTypeCodec = (ValueCodec<T>) codec;
				return realTypeCodec.decode(element, type);
			} catch (Exception e) {
				exceptionHandler.accept(e);
				LOGGER.trace("Decoder exception {}", lazy(codec), e);
			}
		}

		throw exceptionHandler.toException("No suitable decoder found for a type " + type,
			PreferencesCodecException::new);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	protected <T> Element encode(@Nullable T value, @NotNull Class<T> type) throws PreferencesCodecException {
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

			throw new PreferencesCodecException("Failed to cast " + value + " to " + type);
		}

		List<ValueCodec<?>> codecs = this.codecTypeCache.computeIfAbsent(type, this::createCodecCache);

		ExceptionHandler exceptionHandler = new ExceptionHandler();
		for (ValueCodec<?> codec : codecs) {
			try {
				ValueCodec<T> valueCodec = (ValueCodec<T>) codec;
				if (!valueCodec.isSupportedValue(value))
					continue;

				return valueCodec.encode(value, type);
			} catch (Exception e) {
				exceptionHandler.accept(e);
				LOGGER.trace("Encoder exception {}", lazy(codec), e);
			}
		}

		throw exceptionHandler.toException("No suitable encoder found for a type " + type,
			PreferencesCodecException::new);
	}

	@Override
	public void registerCodec(@NotNull ValueCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		Lock lock = this.codecsLock.writeLock();
		lock.lock();
		try {
			if (!this.codecs.add(codec))
				return;
			this.codecTypeCache.clear();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void unregisterCodec(@NotNull ValueCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		Lock lock = this.codecsLock.writeLock();
		lock.lock();
		try {
			if (!this.codecs.remove(codec))
				return;
			this.codecTypeCache.clear();
		} finally {
			lock.unlock();
		}
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<ValueCodec<?>> getCodecs() {
		Lock lock = this.codecsLock.readLock();
		lock.lock();
		try {
			return List.copyOf(this.codecs);
		} finally {
			lock.unlock();
		}
	}
}
