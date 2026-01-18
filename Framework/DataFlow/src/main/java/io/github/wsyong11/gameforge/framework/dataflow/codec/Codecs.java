package io.github.wsyong11.gameforge.framework.dataflow.codec;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.framework.dataflow.codec.codec.Codec;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericCodec;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericInfo;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public interface Codecs {
	@NotNull
	default <T> Element encode(@Nullable T value, @NotNull Class<T> type) throws CodecException {
		Objects.requireNonNull(type, "type is null");
		return this.encode(value, TypeToken.of(type));
	}

	@NotNull
	<T> Element encode(@Nullable T value, @NotNull TypeToken<T> type) throws CodecException;

	@Nullable
	default <T> T decode(@NotNull Element element, @NotNull Class<T> type) throws CodecException {
		Objects.requireNonNull(type, "type is null");
		return this.decode(element, TypeToken.of(type));
	}

	@Nullable
	<T> T decode(@NotNull Element element, @NotNull TypeToken<T> type);

	void addCodec(@NotNull Codec<?> codec);

	void removeCodec(@NotNull Codec<?> codec);

	@NotNull
	@Unmodifiable
	List<Codec<?>> getCodecs();

	void addGenericCodec(@NotNull GenericCodec<?> codec);

	void removeGenericCodec(@NotNull GenericCodec<?> codec);

	@NotNull
	@Unmodifiable
	List<GenericCodec<?>> getGenericCodecs();
}
