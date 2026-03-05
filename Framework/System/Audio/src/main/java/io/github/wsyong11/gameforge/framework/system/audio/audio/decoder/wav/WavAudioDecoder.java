package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.wav;

import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AbstractAudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.FloatBuffer;

public class WavAudioDecoder extends AbstractAudioDecoder {
	public WavAudioDecoder(@NotNull DecodeInfo info) {
		super(info);
	}

	@Override
	protected boolean isSupportHint(@NotNull AudioDecodeHint hint) {
		return false;
	}

	@NotNull
	@Override
	public AudioMetadata getMetadata() throws AudioDecodeException {
		return null;
	}

	@Override
	public int decode(@NotNull FloatBuffer buffer, int maxFrame) throws AudioDecodeException {
		return 0;
	}

	@Override
	public void close() throws IOException {

	}
}
