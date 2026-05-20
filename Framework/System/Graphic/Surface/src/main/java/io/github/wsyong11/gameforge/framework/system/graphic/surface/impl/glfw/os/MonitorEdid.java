package io.github.wsyong11.gameforge.framework.system.graphic.surface.impl.glfw.os;

import io.github.wsyong11.gameforge.framework.platform.Platform;
import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public final class MonitorEdid {
	private static final Map<Platform.OS, Supplier<Provider>> PROVIDERS = Map.of(
		Platform.OS.WINDOWS, Lazy.of(Windows::new)
	);

	private static final Provider provider = PROVIDERS.get(Platform.os).get();

	@NotNull
	public static UUID getEdid(long handler) {
		return provider.get(handler);
	}

	private static class Windows implements Provider {
		@NotNull
		@Override
		public UUID get(long handler) {

		}
	}

	private interface Provider {
		@NotNull
		UUID get(long handler);
	}
}
