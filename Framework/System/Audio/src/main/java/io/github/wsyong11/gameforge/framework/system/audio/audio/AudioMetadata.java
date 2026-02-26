package io.github.wsyong11.gameforge.framework.system.audio.audio;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public interface AudioMetadata {
	Key<Long> LOOP_START = key("LOOPSTART", Long.class, Key.LONG_PARSER);
	Key<Long> LOOP_END = key("LOOPEND", Long.class, Key.LONG_PARSER);

	Key<String> TITLE = key("TITLE");
	Key<String> ARTIST = key("ARTIST");

	@NotNull
	static Key<String> key(@NotNull String key) {
		return key(key, String.class, Key.STRING_PARSER);
	}

	@NotNull
	static <T> Key<T> key(@NotNull String key, @NotNull Class<T> type, @NotNull Function<String, T> parser) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(parser, "parser is null");
		return new Key<>(key, type, parser);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	int getSampleRate();

	int getChannels();

	// 整体采样点数量
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
	String getRaw(@NotNull Key<?> key);

	@Nullable
	<T> T get(@NotNull Key<T> key);

	@Contract("_, _ -> param2")
	@Nullable
	default <T> T get(@NotNull Key<T> key, @Nullable T defaultValue) {
		Objects.requireNonNull(key, "key is null");
		T value = this.get(key);
		return value != null ? value : defaultValue;
	}

	default boolean contains(@NotNull Key<?> key) {
		Objects.requireNonNull(key, "key is null");
		return this.getKeys().contains(key.getKey());
	}

	@NotNull
	@UnmodifiableView
	Set<String> getKeys();

	class Key<T> {
		//@formatter:off
		public static final Function<String, String>  STRING_PARSER  = Function.identity();
		public static final Function<String, Integer> INTEGER_PARSER = numParser(Integer::parseInt);
		public static final Function<String, Long>    LONG_PARSER    = numParser(Long::parseLong);
		public static final Function<String, Float>   FLOAT_PARSER   = numParser(Float::parseFloat);
		public static final Function<String, Double>  DOUBLE_PARSER  = numParser(Double::parseDouble);
		//@formatter:on

		public static final Function<String, byte[]> BASE64_PARSER = t -> {
			if (t == null)
				return null;

			try {
				return Base64.getDecoder().decode(t);
			} catch (IllegalArgumentException e) {
				return null;
			}
		};

		public static final Function<String, URI> URI_PARSER = t -> {
			if (t == null)
				return null;

			try {
				return new URI(t);
			} catch (URISyntaxException e) {
				return null;
			}
		};

		@NotNull
		private static <V extends Number> Function<String, V> numParser(@NotNull Function<String, V> convertor) {
			Objects.requireNonNull(convertor, "convertor is null");
			return t -> {
				if (t == null)
					return null;

				try {
					return convertor.apply(t);
				} catch (NumberFormatException e) {
					return null;
				}
			};
		}

		private final String key;
		private final Class<T> type;
		private final Function<String, T> parser;

		public Key(@NotNull String key, @NotNull Class<T> type, @NotNull Function<String, T> parser) {
			Objects.requireNonNull(key, "key is null");
			Objects.requireNonNull(type, "type is null");
			Objects.requireNonNull(parser, "parser is null");

			this.key = key;
			this.type = type;
			this.parser = parser;
		}

		@NotNull
		public String getKey() {
			return this.key;
		}

		@NotNull
		public Class<T> getType() {
			return this.type;
		}

		@NotNull
		public Function<String, T> getParser() {
			return this.parser;
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
