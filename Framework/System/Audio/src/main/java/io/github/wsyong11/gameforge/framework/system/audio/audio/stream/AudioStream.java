package io.github.wsyong11.gameforge.framework.system.audio.audio.stream;

import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface AudioStream extends Closeable {
	@NotNull
	Audio getAudio();

	@NotNull
	AudioStreamStatus getStatus();

	long getPosition();

	long getAvailable();

	default void seek(long frame) throws AudioDecodeException {
		throw new UnsupportedOperationException();
	}

	default boolean isSeekSupport() {
		return false;
	}

	int read(@NotNull FloatBuffer buffer, int maxFrames) throws AudioDecodeException;

	void reset();

	@Override
	void close();
}
