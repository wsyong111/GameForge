package io.github.wsyong11.gameforge.framework.platform;

import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Optional;

public final class UnsafeAccess {
	private static final Lazy<Unsafe> INSTANCE = Lazy.concurrentOf(() -> {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch (Throwable ignored) {
		}

		try {
			return (Unsafe) Unsafe.class
				.getMethod("getUnsafe")
				.invoke(null);
		} catch (Throwable ignored) {
		}

		return null;
	});

	@Nullable
	public static Unsafe get() {
		return INSTANCE.get();
	}

	@NotNull
	public static Unsafe getRequire() {
		Unsafe instance = INSTANCE.get();
		if (instance == null)
			throw new IllegalStateException("Unsafe not supported");

		return instance;
	}

	@NotNull
	public static Optional<Unsafe> getOptional() {
		return Optional.ofNullable(INSTANCE.get());
	}

	private UnsafeAccess() { /* no-op */ }
}
