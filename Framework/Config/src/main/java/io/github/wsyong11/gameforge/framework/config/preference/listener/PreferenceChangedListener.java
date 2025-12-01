package io.github.wsyong11.gameforge.framework.config.preference.listener;

import io.github.wsyong11.gameforge.framework.config.preference.PreferencesStore;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface PreferenceChangedListener extends IListener {
	default void onPreferenceChanged(@NotNull PreferencesStore store, @NotNull String key) {
	}

	default void onPreferenceChanged(@NotNull PreferencesStore store, @NotNull Set<String> keys) {
		for (String key : keys)
			this.onPreferenceChanged(store, key);
	}
}
