package io.github.wsyong11.gameforge.framework.system.audio.audio;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.UUID;

public interface Audio extends Closeable {
	@NotNull
	UUID getId();

	default float getDurationMs() {
		long totalSamples = this.getTotalSamples();
		if (totalSamples <= 0L)
			return 0.0F;

		return ((float) totalSamples / this.getSampleRate()) * 1000.0F;
	}

	default boolean isStreamable() {
		return this.getTotalSamples() <= 0L;
	}

	long getTotalSamples();

	int getSampleRate();

	int getChannels();

	int getBitDepth();

	@NotNull
	AudioStream newStream();

	@Override
	void close();
}
