package io.github.wsyong11.gameforge.framework.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface Config {
	@NotNull
	@Unmodifiable
	Set<String> getKeys();

	default boolean contains(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.getKeys().contains(key);
	}

	// ============================================================================================================== //

	@Nullable
	Boolean getBoolean(@NotNull String key);

	default boolean getBoolean(@NotNull String key, boolean defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Boolean value = this.getBoolean(key);
		return value == null ? defaultValue : value;
	}

	@Nullable
	Byte getByte(@NotNull String key);

	default byte getByte(@NotNull String key, byte defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Byte value = this.getByte(key);
		return value == null ? defaultValue : value;
	}

	@Nullable
	Short getShort(@NotNull String key);

	default short getShort(@NotNull String key, short defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Short value = this.getShort(key);
		return value == null ? defaultValue : value;
	}

	@Nullable
	Integer getInt(@NotNull String key);

	default int getInt(@NotNull String key, int defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Integer value = this.getInt(key);
		return value == null ? defaultValue : value;
	}

	@Nullable
	Long getLong(@NotNull String key);

	default long getLong(@NotNull String key, long defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Long value = this.getLong(key);
		return value == null ? defaultValue : value;
	}

	@Nullable
	Float getFloat(@NotNull String key);

	default float getFloat(@NotNull String key, float defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Float value = this.getFloat(key);
		return value == null ? defaultValue : value;
	}

	@Nullable
	Double getDouble(@NotNull String key);

	default double getDouble(@NotNull String key, double defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Double value = this.getDouble(key);
		return value == null ? defaultValue : value;
	}

	@Nullable
	String getString(@NotNull String key);

	@NotNull
	default String getString(@NotNull String key, @NotNull String defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(defaultValue, "defaultValue is null");
		String value = this.getString(key);
		return value == null ? defaultValue : value;
	}

	// ============================================================================================================== //

	@Nullable
	@Unmodifiable
	<T> List<T> getList(@NotNull String key, @NotNull Class<T> elementType);

	@NotNull
	@Unmodifiable
	default <T> List<T> getList(@NotNull String key, @NotNull Class<T> elementType, @NotNull List<T> defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(elementType, "elementType is null");
		Objects.requireNonNull(defaultValue, "defaultValue is null");
		List<T> value = this.getList(key, elementType);
		return value == null ? List.copyOf(defaultValue) : value;
	}

	@Nullable
	<T extends Enum<T>> T getEnum(@NotNull String key, @NotNull Class<T> type);

	@NotNull
	@Unmodifiable
	default <T extends Enum<T>> T getEnum(@NotNull String key, @NotNull Class<T> type, @NotNull T defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(defaultValue, "defaultValue is null");
		T value = this.getEnum(key, type);
		return value == null ? defaultValue : value;
	}

	// ============================================================================================================== //

	@NotNull
	Editor edit();

	interface Editor {
		@NotNull
		Editor putBoolean(@NotNull String key, boolean value);

		@NotNull
		Editor putByte(@NotNull String key, byte value);

		@NotNull
		Editor putShort(@NotNull String key, short value);

		@NotNull
		Editor putInt(@NotNull String key, int value);

		@NotNull
		Editor putLong(@NotNull String key, long value);

		@NotNull
		Editor putFloat(@NotNull String key, float value);

		@NotNull
		Editor putDouble(@NotNull String key, double value);

		@NotNull
		Editor putString(@NotNull String key, @NotNull String value);

		@NotNull
		<T> Editor putList(@NotNull String key, @NotNull List<T> value, @NotNull Class<T> elementType);

		@NotNull
		<T extends Enum<T>> Editor putEnum(@NotNull String key, @NotNull T value);

		@NotNull
		Editor delete(@NotNull String key);

		void commit();
	}
}
