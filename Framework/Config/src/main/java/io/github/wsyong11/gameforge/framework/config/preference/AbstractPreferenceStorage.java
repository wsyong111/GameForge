package io.github.wsyong11.gameforge.framework.config.preference;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.framework.config.codec.CodecMap;
import io.github.wsyong11.gameforge.framework.config.codec.ValueCodec;
import io.github.wsyong11.gameforge.framework.config.codec.ValueCodecs;
import io.github.wsyong11.gameforge.framework.config.ex.RuntimeCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public abstract class AbstractPreferenceStorage implements PreferenceStorage {
	private final CodecMap codecs;

	public AbstractPreferenceStorage() {
		this.codecs = new CodecMap();
	}

	@Nullable
	protected <T> T decode(@NotNull Element element, @NotNull Class<T> type) throws RuntimeCodecException {
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(type, "type is null");
		return this.codecs.decode(element, type);
	}

	@Nullable
	protected <T> T decode(@NotNull Element element, @NotNull TypeToken<T> type) throws RuntimeCodecException {
	return null;
	}

	@NotNull
	protected <T> Element encode(@Nullable T value, @NotNull Class<T> type) throws RuntimeCodecException {
		Objects.requireNonNull(type, "type is null");
		return this.codecs.encode(value, type);
	}

	@NotNull
	protected <T> Element encode(@Nullable T value, @NotNull TypeToken<T> type) throws RuntimeCodecException {
		Objects.requireNonNull(type, "type is null");
		return this.codecs.encode(value, type.getRawType());
	}

	@Override
	public void registerCodec(@NotNull ValueCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");
		this.codecs.add(codec);
	}

	@Override
	public void unregisterCodec(@NotNull ValueCodec<?> codec) {
		Objects.requireNonNull(codec, "codec is null");
		this.codecs.remove(codec);
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<ValueCodec<?>> getCodecs() {
		return this.codecs.getCodecs();
	}
}
