package io.github.wsyong11.gameforge.framework.spi;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public interface ExtensionHost {
	<T> void addExtension(@NotNull ExtensionType<T> type, @NotNull T instance);

	default <T> void addExtension(@NotNull Extension<T> extension) {
		Objects.requireNonNull(extension, "extension is null");
		this.addExtension(extension.getType(), extension.get());
	}

	<T> void removeExtension(@NotNull ExtensionType<T> type, @NotNull T instance);

	default <T> void removeExtension(@NotNull Extension<T> extension) {
		Objects.requireNonNull(extension, "extension is null");
		this.removeExtension(extension.getType(), extension.get());
	}

	<T> void removeExtension(@NotNull T instance);
}
