package io.github.wsyong11.gameforge.framework.system.audio.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

public interface Audio extends Closeable {
	/**
	 * 获取音频读取的资源路径
	 *
	 * @return 音频资源路径
	 */
	@NotNull
	Identifier getLocation();

	/**
	 * 获取音频元数据，如果音频状态不是 {@link AudioStatus#READY} 则会抛出错误
	 *
	 * @return 音频元数据
	 * @throws IllegalStateException 当音频状态不为 {@link AudioStatus#READY} 时抛出
	 * @see #getStatus()
	 */
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

		default void onFailed() { /* no-op */ }

		default void onClosed() { /* no-op */ }
	}
}
