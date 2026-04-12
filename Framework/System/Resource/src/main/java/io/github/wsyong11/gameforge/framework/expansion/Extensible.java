package io.github.wsyong11.gameforge.framework.expansion;

import org.jetbrains.annotations.NotNull;

public interface Extensible {
	<T> void addExtension(@NotNull ExtensionType<T> type, @NotNull T instance);

	<T> void removeExtension(@NotNull ExtensionType<T> type, @NotNull T instance);
}
