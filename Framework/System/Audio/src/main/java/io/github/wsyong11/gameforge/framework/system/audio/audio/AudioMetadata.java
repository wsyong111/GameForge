package io.github.wsyong11.gameforge.framework.system.audio.audio;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Objects;
import java.util.Set;

public interface AudioMetadata {
	Key<Long> LOOP_START = key("LOOPSTART", Long.class);
	Key<Long> LOOP_END = key("LOOPEND", Long.class);

	Key<String> TITLE = key("TITLE", String.class);
	Key<String> ARTIST = key("ARTIST", String.class);

	@NotNull
	static <T> Key<T> key(@NotNull String key, @NotNull Class<T> type) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(type, "type is null");
		return new Key<>(key, type);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	int getSampleRate();

	int getChannels();

	int getBitDepth();

	long getTotalSamples();

	default long getDurationMs() {
		long totalSamples = this.getTotalSamples();
		if (totalSamples < 0)
			return -1L;
		return (totalSamples * 1000L) / this.getSampleRate();
	}

	boolean isStreamable();

	boolean isSeekable();

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	AudioCategory getCategory();

	// -------------------------------------------------------------------------------------------------------------- //

	default boolean hasLoop() {
		return this.contains(LOOP_START) && this.contains(LOOP_END);
	}

	default long getLoopStartSample() {
		return this.get(LOOP_START, -1L);
	}

	default long getLoopEndSample() {
		return this.get(LOOP_END, -1L);
	}

	@Nullable
	<T> T get(@NotNull Key<T> key);

	@Contract("_, _ -> param2")
	@Nullable
	default <T> T get(@NotNull Key<T> key,@Nullable T defaultValue) {
		Objects.requireNonNull(key, "key is null");
		T value = this.get(key);
		return value!=null?value:defaultValue;
	}

	default boolean contains(@NotNull Key<?> key) {
		Objects.requireNonNull(key, "key is null");
		return this.getKeys().contains(key);
	}

	@NotNull
	@UnmodifiableView
	Set<Key<?>> getKeys();

	class Key<T> {
		private final String key;
		private final Class<T> type;

		public Key(@NotNull String key, @NotNull Class<T> type) {
			Objects.requireNonNull(key, "key is null");
			Objects.requireNonNull(type, "type is null");

			this.key = key;
			this.type = type;
		}

		@NotNull
		public String getKey() {
			return this.key;
		}

		@NotNull
		public Class<T> getType() {
			return this.type;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Key<?> that = (Key<?>) o;
			return Objects.equals(this.key, that.key)
				&& Objects.equals(this.type, that.type);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.key, this.type);
		}

		@Override
		public String toString() {
			return "Key<" + this.type.getName() + ">(\"" + StringEscapeUtils.escapeJava(this.key) + "\")";
		}
	}
}
