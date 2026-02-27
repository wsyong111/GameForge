package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;

@Deprecated
public interface PCMStream extends AutoCloseable {
	int read(@NotNull FloatBuffer buffer, int maxSamples) throws AudioDecodeException;

	boolean isSeekable();

	void seek(long sampleIndex) throws AudioDecodeException;

	long getPosition();

	@Override
	void close();
}
