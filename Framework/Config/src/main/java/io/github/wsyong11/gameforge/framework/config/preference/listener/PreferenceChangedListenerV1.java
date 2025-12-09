package io.github.wsyong11.gameforge.framework.config.preference.listener;

import io.github.wsyong11.gameforge.framework.config.preference.PreferencesStoreV1;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Deprecated
public interface PreferenceChangedListenerV1 extends IListener {
	default void onPreferenceChanged(@NotNull PreferencesStoreV1 store, @NotNull String key) {
	}

	default void onPreferenceChanged(@NotNull PreferencesStoreV1 store, @NotNull Set<String> keys) {
		for (String key : keys)
			this.onPreferenceChanged(store, key);
	}
}
