package io.github.wsyong11.gameforge.framework.spi;

public interface ExtensionLifecycle {
	default void attach() { /* no-op */ }

	default void detach() { /* no-op */ }
}
