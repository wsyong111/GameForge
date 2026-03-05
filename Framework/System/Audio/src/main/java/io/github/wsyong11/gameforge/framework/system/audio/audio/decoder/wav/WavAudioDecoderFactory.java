package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.wav;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypes;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public class WavAudioDecoderFactory implements AudioDecoderFactory {
	@NotNull
	@Override
	public AudioDecoder create(@NotNull AudioDecoder.DecodeInfo info) {
		Objects.requireNonNull(info, "info is null");
		return null;
	}

	@NotNull
	@Override
	public Set<MimeType> getSupportMimes() {
		return Set.of(MimeTypes.Audio.WAV);
	}

	@NotNull
	@Override
	public Set<AudioDecodeHint> getSupportHints() {
		return Set.of();
	}
}
