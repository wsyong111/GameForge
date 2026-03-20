package io.github.wsyong11.gameforge.framework.system.audio.audio.stream;

import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.nio.ByteBuffer;

public interface AudioStream extends Closeable {
	@NotNull
	Audio getAudio();

	@NotNull
	AudioStreamStatus getStatus();

	long getPosition();

	long getAvailable();

	default void seek(long frame) {
		throw new UnsupportedOperationException();
	}

	default boolean isSeekSupport() {
		return false;
	}

	int read(@NotNull ByteBuffer buffer, int maxSamples) throws AudioDecodeException;

	void reset();

	@Override
	void close();
}
