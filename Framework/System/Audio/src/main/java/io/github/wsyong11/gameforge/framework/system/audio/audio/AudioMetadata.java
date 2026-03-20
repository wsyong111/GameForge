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

/**
 * 音频元数据接口。
 * <p>
 * 表示音频文件或音频流的基本属性和可选标签信息。
 * 提供对音频循环点、标题、艺术家等信息的访问。
 * </p>
 * <p>
 * 重要概念：
 * <ul>
 *     <li>Frame（帧）：一组同时采样的所有通道采样点</li>
 *     <li>Sample（采样点）：单个通道的采样值</li>
 *     <li>sampleRate：每秒每个通道的采样点数（Hz）</li>
 *     <li>frameRate：每秒帧数（Hz），每帧包含所有通道采样点</li>
 * </ul>
 * 默认实现假设每帧包含每个通道 1 个 sample，因此 {@link #getSampleRate()} 等于 {@link #getFrameRate()}。
 * </p>
 */
public interface AudioMetadata {
	/**
	 * 循环起始帧索引键。
	 * <p>
	 * 用于表示音频循环的起始帧。单位为采样点（Sample）。
	 * </p>
	 */
	Key<Long> LOOP_START = key("LOOPSTART", Long.class, Key.LONG_PARSER);

	/**
	 * 循环起始帧索引键。
	 * <p>
	 * 用于表示音频循环的起始帧。单位为采样点（Sample）。
	 * </p>
	 */
	Key<Long> LOOP_END = key("LOOPEND", Long.class, Key.LONG_PARSER);

	/**
	 * 音频标题键。
	 */
	Key<String> TITLE = key("TITLE");

	/**
	 * 音频标题键。
	 */
	Key<String> ARTIST = key("ARTIST");

	/**
	 * 创建一个字符串类型的 {@link Key}。
	 *
	 * @param key 键名
	 * @return 对应的 Key 对象
     */
	@NotNull
	static Key<String> key(@NotNull String key) {
		return key(key, String.class, Key.STRING_PARSER);
	}

	/**
	 * 创建一个指定类型的 {@link Key}。
	 *
	 * @param key    键名
	 * @param type   数据类型
	 * @param parser 字符串解析函数
	 * @param <T>    类型参数
	 * @return 对应的 Key 对象
     */
	@NotNull
	static <T> Key<T> key(@NotNull String key, @NotNull Class<T> type, @NotNull Function<String, T> parser) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(parser, "parser is null");
		return new Key<>(key, type, parser);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取音频帧率（每秒帧数，Hz）。
	 *
	 * @return 音频帧率
	 */
	int getFrameRate();

	/**
	 * 获取音频采样率（每秒每通道采样数，Hz）。
	 * <p>
	 * 默认实现返回 {@link #getFrameRate()}。
	 * </p>
	 *
	 * @return 音频采样率
	 */
	default int getSampleRate() {
		return this.getFrameRate();
	}

	/**
	 * 获取音频通道数。
	 *
     * @return 通道数
	 */
	int getChannels();

	/**
	 * 获取音频总帧数。
	 *
	 * @return 总帧数，若总帧数未知返回 {@code -1}
	 */
	long getTotalFrames();

	/**
	 * 获取音频总采样数。
	 * <p>
	 * 等价于 {@code getTotalFrames() * getChannels()}。
	 * </p>
	 *
	 * @return 总采样数，若总采样数未知返回 {@code -1}
	 */
	default long getTotalSamples() {
		return this.getTotalFrames() * this.getChannels();
	}

	/**
	 * 获取音频时长（毫秒）。
	 *
	 * @return 音频时长，若总帧数未知返回 {@code -1}
     */
	default long getDurationMs() {
		long totalFrames = this.getTotalFrames();
		if (totalFrames < 0)
			return -1L;
		return (totalFrames * 1000L) / this.getFrameRate();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取音频分类信息。
	 *
	 * @return {@link AudioCategory} 枚举
     */
	@NotNull
	AudioCategory getCategory();

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 判断音频是否有循环信息。
	 *
	 * @return 如果同时包含 {@link #LOOP_START} 和 {@link #LOOP_END} 返回 {@code true}
     */
	default boolean hasLoop() {
		return this.contains(LOOP_START) && this.contains(LOOP_END);
	}

	/**
	 * 获取循环起点采样索引。
	 *
	 * @return 循环起点采样索引，若不存在返回 {@code -1}
     */
	default long getLoopStartSample() {
		return this.get(LOOP_START, -1L);
	}

	/**
	 * 获取循环终点采样索引。
	 *
	 * @return 循环终点采样索引，若不存在返回 {@code -1}
     */
	default long getLoopEndSample() {
		return this.get(LOOP_END, -1L);
	}

	/**
	 * 获取原始元数据字符串值。
	 *
	 * @param key 元数据键
	 * @return 对应字符串值，若不存在返回 {@code null}
     */
	@Nullable
	String getRaw(@NotNull Key<?> key);

	/**
	 * 获取指定类型的元数据值。
	 *
	 * @param key 元数据键
	 * @param <T> 值类型
	 * @return 对应值，若不存在返回 {@code null}
     */
	@Nullable
	<T> T get(@NotNull Key<T> key);

	/**
	 * 获取指定类型的元数据值，若不存在则返回默认值。
	 *
	 * @param key          元数据键
	 * @param defaultValue 默认值
	 * @param <T>          值类型
	 * @return 对应值或默认值
     */
	@Contract("_, _ -> param2")
	@Nullable
	default <T> T get(@NotNull Key<T> key, @Nullable T defaultValue) {
		Objects.requireNonNull(key, "key is null");
		T value = this.get(key);
		return value != null ? value : defaultValue;
	}

	/**
	 * 判断是否包含指定元数据键。
	 *
	 * @param key 元数据键
	 * @return 如果存在返回 {@code true}
     */
	default boolean contains(@NotNull Key<?> key) {
		Objects.requireNonNull(key, "key is null");
		return this.getKeys().contains(key.getKey());
	}

	/**
	 * 获取所有已存在的元数据键。
	 *
	 * @return 不可修改视图，包含所有键名
     */
	@NotNull
	@UnmodifiableView
	Set<String> getKeys();

	/**
	 * 音频元数据键对象。
	 *
     * @param <T> 值类型
     */
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

		/**
		 * 构造 Key 对象。
		 *
		 * @param key    键名
		 * @param type   值类型
		 * @param parser 字符串解析器
         */
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
