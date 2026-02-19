package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypes;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Set;

public class OGGAudioDecoder implements AudioDecoder {
	private static final Set<MimeType> SUPPORT_TYPE = Set.of(MimeTypes.Audio.OGG);

	@NotNull
	@Override
	public Set<MimeType> getSupportMimeType() {
		return SUPPORT_TYPE;
	}

	@NotNull
	@Override
	public  AudioMetadata parseMetadata(@NotNull InputStream stream) throws AudioDecodeException {
		return null;seek
	}

	@Override
	public @NotNull AudioStream decode(@NotNull InputStream stream) throws AudioDecodeException {
		return null;
	}

	@Override
	public boolean isSupportStreaming() {
		return false;
	}

	@Override
	public @NotNull AudioStream decodeStream(@NotNull InputStream stream) throws AudioDecodeException {
		return null;
	}
}
