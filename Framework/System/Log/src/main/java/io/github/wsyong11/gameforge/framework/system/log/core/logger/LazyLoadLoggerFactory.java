package io.github.wsyong11.gameforge.framework.system.log.core.logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
@Internal
public class LazyLoadLoggerFactory {
	private static final LoadingCache<Pair<ClassLoader, String>, Logger> CACHE = CacheBuilder
		.newBuilder()
		.weakKeys()
		.build(CacheLoader.from(k -> {
			assert k != null;
			return new LazyLoadLoggerImpl(k.getLeft(), k.getRight());
		}));

	@NotNull
	public static Logger get(@NotNull ClassLoader classLoader, @NotNull String name) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		Objects.requireNonNull(name, "name is null");
		return CACHE.getUnchecked(Pair.ofNonNull(classLoader, name));
	}
}
