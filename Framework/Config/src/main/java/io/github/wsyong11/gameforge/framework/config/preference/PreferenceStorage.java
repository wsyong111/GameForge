package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.config.preference.listener.PreferenceChangedListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface PreferenceStorage {
	@Nullable
	<T> T getValue(@NotNull String key, @NotNull Class<T> type);

	@Nullable
	@Contract("_, _, !null -> !null; _, _, null -> _")
	default <T> T getValue(@NotNull String key, @NotNull Class<T> type, @Nullable T defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(type, "type is null");

		T value = this.getValue(key, type);
		return value == null ? defaultValue : value;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	void addChangeListener(@NotNull PreferenceChangedListener listener);

	void removeChangeListener(@NotNull PreferenceChangedListener listener);

	// -------------------------------------------------------------------------------------------------------------- //

	void registerCodec(@NotNull ValueCodec<?> codec);

	void unregisterCodec(@NotNull ValueCodec<?> codec);

	@NotNull
	List<ValueCodec<?>> getCodecs();

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	Editor edit();

	interface Editor {
		@NotNull
		<T> Editor setValue(@NotNull String key, @Nullable T value);

		void cancel();

		void apply();
	}
}
