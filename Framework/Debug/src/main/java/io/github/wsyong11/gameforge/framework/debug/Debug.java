package io.github.wsyong11.gameforge.framework.debug;

import io.github.wsyong11.gameforge.framework.env.EnvConfig;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.collection.CollectionUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;
import static io.github.wsyong11.gameforge.util.ObjectUtils.safeToString;

public final class Debug {
	private static final Logger LOGGER = Log.getLogger();

	@Nullable
	private static final Debug INSTANCE = EnvConfig.DEBUG.getValue()
		? new Debug()
		: null;

	private static final DebugInstanceProvider<?> NOOP_PROVIDER = new DebugInstanceProvider<>() {
		@NotNull
		@Override
		public Class<Object> getSupportClass() {
			return Object.class;
		}

		@NotNull
		@Override
		public Object wrap(@NotNull Object instance) {
			return instance;
		}
	};

	// -------------------------------------------------------------------------------------------------------------- //

	@Contract("_, null -> null; _, !null -> !null")
	@Nullable
	public static <T, V extends T> T wrap(@NotNull Class<T> type, @Nullable V instance) {
		Objects.requireNonNull(type, "type is null");
		if (INSTANCE == null || instance == null)
			return instance;

		DebugInstanceProvider<T> provider = INSTANCE.getProvider(type, instance);

		try {
			return provider.wrap(instance);
		} catch (Throwable e) {
			LOGGER.debug("Exception occurred while wrapping {} using provider {}",
				lazy(() -> safeToString(instance)),
				lazy(() -> safeToString(provider)),
				e);
			return instance;
		}
	}

	public static <T> void dump(@NotNull Class<T> type, @Nullable T instance) {
		Objects.requireNonNull(type, "type is null");
		if (INSTANCE == null || instance == null)
			return;

		DebugInstanceProvider<T> provider = INSTANCE.getProvider(type, instance);

		StringBuilder sb = new StringBuilder();
		try {
			provider.dump(sb, instance);
		} catch (Throwable e) {
			LOGGER.debug("Exception occurred while dumping {} using provider {}",
				lazy(() -> safeToString(instance)),
				lazy(() -> safeToString(provider)),
				e);
			return;
		}
		LOGGER.debug("Debug dump from instance {}\n{}", safeToString(instance), sb.toString());
	}

	public static <T> void dump(@NotNull StringBuilder sb, @NotNull Class<T> type, @Nullable T instance) {
		Objects.requireNonNull(sb, "sb is null");
		Objects.requireNonNull(type, "type is null");

		if (INSTANCE == null || instance == null)
			return;

		INSTANCE.getProvider(type, instance).dump(sb, instance);
	}

	private final Map<Class<?>, List<DebugInstanceProvider<?>>> providerMap;

	private Debug() {
		LOGGER.debug("Scanning SPI...");

		this.providerMap = new HashMap<>();

		try {
			this.scanSpi();
		} catch (Throwable e) {
			LOGGER.warn("Scan SPI failed");
		}
	}

	@SuppressWarnings("rawtypes")
	private void scanSpi() {
		this.providerMap.clear();

		long startTimeNs = System.nanoTime();

		ServiceLoader<DebugInstanceProvider> loader = ServiceLoader.load(DebugInstanceProvider.class);

		int counter = 0;
		Iterator<DebugInstanceProvider> iterator = loader.iterator();
		while (iterator.hasNext()) {
			DebugInstanceProvider<?> provider;
			try {
				provider = iterator.next();
			} catch (Throwable e) {
				LOGGER.warn("Failed to load debug instance provider", e);
				continue;
			}

			Class<?> supportClass;
			try {
				supportClass = provider.getSupportClass();
			} catch (Throwable e) {
				LOGGER.warn("Cannot get support class from provider {}", safeToString(provider), e);
				continue;
			}

			this.providerMap.computeIfAbsent(supportClass, k -> new ArrayList<>()).add(provider);
			counter++;
		}

		long tookTimeNs = System.nanoTime() - startTimeNs;

		LOGGER.debug("{} debug instance provider loaded, took {} ms", counter, tookTimeNs / 1000 / 1000);
	}

	@NotNull
	private <T> List<DebugInstanceProvider<T>> getProviders(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		List<DebugInstanceProvider<?>> providers = this.providerMap.get(type);
		return providers == null ? List.of() : CollectionUtils.forceCast(providers);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private <T> DebugInstanceProvider<T> getProvider(@NotNull Class<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		for (DebugInstanceProvider<T> provider : this.getProviders(type)) {
			try {
				if (provider.canProcess(instance))
					return provider;
			} catch (Throwable e) {
				LOGGER.debug("An exception occurred in the canProcess function of provider {}", safeToString(provider), e);
			}
		}
		return (DebugInstanceProvider<T>) NOOP_PROVIDER;
	}
}
