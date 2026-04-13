package io.github.wsyong11.gameforge.framework.spi;

import org.jetbrains.annotations.NotNull;

public interface Extension<T> {
	@NotNull
	ExtensionType<T> getType();

	@NotNull
	T get();
}
