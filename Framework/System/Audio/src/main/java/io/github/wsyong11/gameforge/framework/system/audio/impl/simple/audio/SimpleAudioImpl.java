package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioStatus;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SimpleAudioImpl implements Audio {

	@Override
	public @NotNull UUID getId() {
		return null;
	}

	@Override
	public @NotNull AudioMetadata getMetadata() {
		return null;
	}

	@Override
	public @NotNull AudioStatus getStatus() {
		return null;
	}

	@Override
	public @NotNull AudioStream newStream() {
		return null;
	}

	@Override
	public @NotNull AudioStream openStreamingStream() {
		return null;
	}

	@Override
	public void close() {

	}
}
