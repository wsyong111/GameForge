package io.github.wsyong11.gameforge.framework.dataflow.codec;

import com.google.common.collect.Streams;
import io.github.wsyong11.gameforge.framework.dataflow.codec.codec.Codec;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericCodec;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericInfo;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.NullElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.StreamUtils;
import io.github.wsyong11.gameforge.util.collection.CollectionUtils;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import io.github.wsyong11.gameforge.util.reflect.ReflectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public abstract class AbstractCodecs implements Codecs {
	private static final Logger LOGGER = Log.getLogger();

	private final Set<Codec<?>> codecList;
	private final Map<Class<?>, List<Codec<?>>> codecTypeCache;
	private final Map<Codec<?>, Set<Class<?>>> codecSupportTypeCache;

	private final Set<GenericCodec<?>> genericCodecList;
	private final Set<GenericInfo<?>> genericInfoList;
	private final Map<Class<?>, List<GenericInfo<?>>> genericInfoCache;

	@Nullable
	private volatile List<Class<?>> allSupportTypeCache;

	public AbstractCodecs() {
		this.codecList = new LinkedHashSet<>();
		this.codecTypeCache = new ConcurrentHashMap<>();
		this.codecSupportTypeCache = new ConcurrentHashMap<>();

		this.genericCodecList = new LinkedHashSet<>();
		this.genericInfoList = new LinkedHashSet<>();
		this.genericInfoCache = new ConcurrentHashMap<>();

		this.allSupportTypeCache = null;
	}

	@NotNull
	@Unmodifiable
	protected List<Class<?>> getAllSupportTypes() {
		List<Class<?>> supportTypes = this.allSupportTypeCache;

		if (supportTypes == null) {
			synchronized (this) {
				supportTypes = this.allSupportTypeCache;
				if (supportTypes == null)
					this.allSupportTypeCache = supportTypes = this.calcAllSupportTypes();
			}
		}

		return supportTypes;
	}

	@NotNull
	@Unmodifiable
	private List<Class<?>> calcAllSupportTypes() {
		// Codec -> GenericCodec

		List<Codec<?>> codecs;
		synchronized (this.codecList) {
			codecs = List.copyOf(this.codecList);
		}

		List<GenericInfo<?>> genericInfos;
		synchronized (this.genericInfoList) {
			genericInfos = List.copyOf(this.genericInfoList);
		}

		return Streams
			.concat(codecs
					.stream()
					.map(this::getCodecSupportTypes)
					.flatMap(Set::stream),
				genericInfos
					.stream()
					.map(GenericInfo::getType)
			)
			.filter(StreamUtils.distinct())
			.toList();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@Unmodifiable
	protected Set<Class<?>> getCodecSupportTypes(@NotNull Codec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");
		return this.codecSupportTypeCache.getOrDefault(codec, Set.of());
	}

	@NotNull
	@Unmodifiable
	private List<Codec<?>> resolveCodecsForType(@NotNull Class<?> type) {
		Objects.requireNonNull(type, "type is null");

		List<Codec<?>> list;
		synchronized (this.codecList) {
			list = List.copyOf(this.codecList);
		}

		return list
			.stream()
			.filter(codec -> this
				.getCodecSupportTypes(codec)
				.stream()
				.anyMatch(supportType -> supportType.isAssignableFrom(type)))
			.filter(codec -> {
				try {
					return codec.isSupportType(type);
				} catch (Exception e) {
					LOGGER.warn("Exception in codec {} when checking support type", lazy(codec), e);
					return false;
				}
			})
			.toList();
	}

	@NotNull
	@Unmodifiable
	protected <T> List<Codec<T>> getCodec(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		return CollectionUtils.forceCast(this.codecTypeCache.computeIfAbsent(type, this::resolveCodecsForType));
	}

	@NotNull
	protected <T> Element encodeWithCodec(@NotNull CodecContext ctx, @Nullable T value, @NotNull Class<T> type) throws CodecException {
		Objects.requireNonNull(ctx, "ctx is null");
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

			throw new CodecException("Failed to cast " + value + " to " + type);
		}

		if (!type.isInstance(value))
			throw new CodecException(value.getClass().getName() + " is not instance of " + type.getName());

		ExceptionHandler exceptionHandler = new ExceptionHandler();
		for (Codec<T> codec : this.getCodec(type)) {
			try {
				if (!codec.isSupportValue(ctx, value, type))
					continue;

				return codec.encode(ctx, value, type);
			} catch (Exception e) {
				exceptionHandler.accept(e);
				LOGGER.trace("Encoder exception {}", lazy(codec), e);
			}
		}

		throw exceptionHandler.toException("No suitable encoder found for a type " + type,
			CodecException::new);
	}

	@Nullable
	protected <T> T decodeWithCodec(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<T> type) throws CodecException {
		Objects.requireNonNull(ctx, "ctx is null");
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
			throw new CodecException("Failed to cast " + element + " to " + type, e);
		}

		ExceptionHandler exceptionHandler = new ExceptionHandler();

		for (Codec<T> codec : this.getCodec(type)) {
			try {
				if (!codec.isSupportElement(ctx, element, type))
					continue;

				return codec.decode(ctx, element, type);
			} catch (Exception e) {
				exceptionHandler.accept(e);
				LOGGER.trace("Decoder exception {}", lazy(codec), e);
			}
		}

		throw exceptionHandler.toException("No suitable decoder found for a type " + type,
			CodecException::new);
	}

	@Override
	public void addCodec(@NotNull Codec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		synchronized (this.codecList) {
			if (!this.codecList.add(codec))
				return;
		}

		Set<Class<?>> supportTypes;
		try {
			supportTypes = Set.copyOf(codec.getSupportTypes());
		} catch (Exception e) {
			synchronized (this.codecList) {
				this.codecList.remove(codec);
			}
			throw e;
		}

		if (supportTypes.isEmpty())
			LOGGER.warn("Codec {} support type set is empty, do you know what you are doing?",
				lazy(codec));

		this.codecSupportTypeCache.put(codec, supportTypes);
		this.codecTypeCache.clear();
		this.allSupportTypeCache = null;
	}

	@Override
	public void removeCodec(@NotNull Codec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		synchronized (this.codecList) {
			if (!this.codecList.remove(codec))
				return;
		}

		this.codecTypeCache.entrySet().removeIf(entry -> {
			List<Codec<?>> cache = entry.getValue();
			return cache.isEmpty() || cache.contains(codec);
		});

		this.codecSupportTypeCache.remove(codec);
		this.allSupportTypeCache = null;
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<Codec<?>> getCodecs() {
		synchronized (this.codecList) {
			return List.copyOf(this.codecList);
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	private List<GenericInfo<?>> resolveGenericInfo(@NotNull Class<?> type) {
		Objects.requireNonNull(type, "type is null");

		List<GenericInfo<?>> list;
		synchronized (this.genericInfoList) {
			list = List.copyOf(this.genericInfoList);
		}

		return list
			.stream()
			.filter(info -> {
				Class<?> infoType = info.getType();
				return infoType == type || infoType.isAssignableFrom(type);
			})
			.sorted(Comparator.comparingInt(info ->
				ReflectUtils.getInheritanceDistance(type, info.getType())))
			.toList();
	}

	@NotNull
	@Unmodifiable
	protected <T> List<GenericInfo<? extends T>> findGenericInfo(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		return CollectionUtils.forceCast(this.genericInfoCache.computeIfAbsent(type, this::resolveGenericInfo));
	}

	@Override
	public void addGenericCodec(@NotNull GenericCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		synchronized (this.genericCodecList) {
			if (!this.genericCodecList.add(codec))
				return;
		}

		GenericInfo<? super Object> info;

		try {
			@SuppressWarnings("unchecked")
			GenericCodec<Object> unsafeCodec = (GenericCodec<Object>) codec;
			Class<? super Object> type = unsafeCodec.getType();
			GenericInfo.Builder<? super Object> builder = new GenericInfo.Builder<>(type, unsafeCodec);
			unsafeCodec.buildInfo(builder);
			info = builder.build();
		} catch (Exception e) {
			synchronized (this.genericCodecList) {
				this.genericCodecList.remove(codec);
			}

			throw e;
		}

		synchronized (this.genericInfoList) {
			this.genericInfoList.add(info);
		}

		this.genericInfoCache.clear();
		this.allSupportTypeCache = null;
	}

	@Override
	public void removeGenericCodec(@NotNull GenericCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");

		synchronized (this.genericCodecList) {
			if (!this.genericCodecList.remove(codec))
				return;
		}

		synchronized (this.genericInfoList) {
			this.genericInfoList.removeIf(info ->
				Objects.equals(info.getCodec(), codec));
		}

		this.genericInfoCache.entrySet().removeIf(entry -> {
			List<GenericInfo<?>> cache = entry.getValue();
			if (cache.isEmpty())
				return true;

			for (GenericInfo<?> info : cache) {
				if (Objects.equals(info.getCodec(), codec))
					return true;
			}

			return false;
		});

		this.allSupportTypeCache = null;
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<GenericCodec<?>> getGenericCodecs() {
		return List.copyOf(this.genericCodecList);
	}
}
