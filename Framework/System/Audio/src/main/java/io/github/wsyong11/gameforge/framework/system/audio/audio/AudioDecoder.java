package io.github.wsyong11.gameforge.framework.system.audio.audio;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Set;

public interface AudioDecoder {
	@NotNull
	Set<MimeType> getSupportMimeType();

	@NotNull
	AudioMetadata parseMetadata(@NotNull InputStream stream) throws AudioDecodeException;

	@NotNull
	AudioStream decode(@NotNull InputStream stream) throws AudioDecodeException;

	boolean isSupportStreaming();

	@NotNull
	AudioStream decodeStream(@NotNull InputStream stream) throws  AudioDecodeException;
}
