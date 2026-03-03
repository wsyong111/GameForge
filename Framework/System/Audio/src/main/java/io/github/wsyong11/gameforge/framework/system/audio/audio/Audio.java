package io.github.wsyong11.gameforge.framework.system.audio.audio;

import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.UUID;

public interface Audio extends Closeable {
	@NotNull
	UUID getId();

	@NotNull
	AudioMetadata getMetadata();

	@NotNull
	AudioStatus getStatus();

	@NotNull
	AudioStream newStream();

	@NotNull
	AudioStream openStreamingStream();

	@Override
	void close();
}
