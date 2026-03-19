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

	long getPositionSamples();

	long getAvailableSamples();

	void seek(long sampleIndex) throws UnsupportedOperationException;

	void reset();

	int read(@NotNull ByteBuffer buffer, int maxSamples) throws AudioDecodeException;

	@Override
	void close();
}
