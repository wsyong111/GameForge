package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.ogg;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypes;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Objects;
import java.util.Set;

public class OggAudioDecoderFactory implements AudioDecoderFactory {
	public static final Lazy<OggAudioDecoderFactory> INSTANCE = Lazy.concurrentOf(OggAudioDecoderFactory::new);

	@NotNull
	@Override
	public AudioDecoder create(@NotNull AudioDecoder.DecodeInfo info) {
		Objects.requireNonNull(info, "info is null");

		Set<AudioDecodeHint> hints = info.getHints();
		if (hints.contains(AudioDecodeHint.STREAMING))
			throw new UnsupportedOperationException();

		return new OggAudioDecoder(info);
	}

	@NotNull
	@Override
	public Set<MimeType> getSupportMimes() {
		return Set.of(MimeTypes.Audio.OGG);
	}

	@NotNull
	@Unmodifiable
	@Override
	public Set<AudioDecodeHint> getSupportHints() {
		return Set.of(AudioDecodeHint.STREAMING, AudioDecodeHint.PRELOAD_METADATA);
	}
}
