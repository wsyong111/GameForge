package io.github.wsyong11.gameforge.framework.system.audio.audio;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface AudioStream extends Closeable {
	long getTotalSamples();

	long getPosition();

	boolean isFinished();

	float availableMs();

	void seek(long sampleIndex) throws UnsupportedOperationException;

	void reset();

	int read(@NotNull ByteBuffer buffer, int maxSamples) throws IOException;

	@Override
	void close();
}
