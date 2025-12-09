package io.github.wsyong11.gameforge.framework.config.preference.listener;

import io.github.wsyong11.gameforge.framework.config.preference.PreferenceStorage;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public interface PreferenceChangedListener extends IListener {
	default void onPreferenceChanged(@NotNull PreferenceStorage storage, @NotNull String key) {
	}

	default void onPreferenceChanged(@NotNull PreferenceStorage storage, @NotNull Set<String> keys) {
		Objects.requireNonNull(storage, "storage is null");
		Objects.requireNonNull(keys, "keys is null");

		for (String key : keys)
			this.onPreferenceChanged(storage, key);
	}
}
