package io.github.wsyong11.gameforge.framework.system.audio.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

public interface Audio extends Closeable {
	@NotNull
	Identifier getLocation();

	@NotNull
	AudioMetadata getMetadata();

	@NotNull
	AudioStatus getStatus();

	@NotNull
	AudioStream newStream();

	@NotNull
	AudioStream openStreamingStream();

	void registerStatusCallback(@NotNull StatusCallback callback);

	void unregisterStatusCallback(@NotNull StatusCallback callback);

	@Override
	void close();

	interface StatusCallback {
		default void onReady() { /* no-op */ }

		default void onFailed(@NotNull Throwable exception) { /* no-op */ }

		default void onClosed() { /* no-op */ }
	}
}
