package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface AudioDecoderFactory {
	@NotNull
	AudioDecoder create(@NotNull AudioDecoder.DecodeInfo info);

	@NotNull
	Set<MimeType> getSupportMimes();

	@NotNull
	Set<AudioDecodeHint> getSupportHints();
}
