package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.FloatBuffer;

public interface PCMStream extends AutoCloseable {
	int read(@NotNull FloatBuffer buffer, int maxSamples) throws IOException, AudioDecodeException;

	boolean isSeekable();

	void seek(long sampleIndex) throws IOException;

	long getPosition();

	@Override
	void close();
}
