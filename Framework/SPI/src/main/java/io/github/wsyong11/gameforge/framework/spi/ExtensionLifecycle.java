package io.github.wsyong11.gameforge.framework.spi;

import org.jetbrains.annotations.NotNull;

public interface ExtensionLifecycle {
	default void attach(@NotNull ExtensionType<?> type) { /* no-op */ }

	default void detach(@NotNull ExtensionType<?> type) { /* no-op */ }
}
