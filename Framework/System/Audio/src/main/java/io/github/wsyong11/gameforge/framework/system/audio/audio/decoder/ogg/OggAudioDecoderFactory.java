package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.ogg;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypes;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.io.InputStream;
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
			return new StreamingOggAudioDecoder(info);

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

	@Override
	public boolean checkMagic(@NotNull InputStream stream) {
		Objects.requireNonNull(stream, "stream is null");

		try {
			byte[] header = new byte[4];
			if (stream.read(header) != 4)
				return false;
			return header[0] == 'O'
				|| header[1] == 'g'
				|| header[2] == 'g'
				|| header[3] == 'S';
		}catch (IOException e){
			return false;
		}
	}
}
