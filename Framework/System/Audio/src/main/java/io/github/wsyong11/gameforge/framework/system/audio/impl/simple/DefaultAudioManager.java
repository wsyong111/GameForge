package io.github.wsyong11.gameforge.framework.system.audio.impl.simple;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultAudioManager implements AudioManager, AutoCloseable {
	private final ExecutorService decoderThreadPool;

	private final Map<Identifier, Audio> audioCache;

	public DefaultAudioManager() {
		int decoderThreadCount = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors() - 2, 4));
		this.decoderThreadPool = new ThreadPoolExecutor(
			1,
			decoderThreadCount,
			10,
			TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(128),
			new AudioDecoderThreadFactory(),
			new ThreadPoolExecutor.CallerRunsPolicy()
		);

		this.audioCache = new ConcurrentHashMap<>();
	}

	@Nullable
	@Override
	public Audio getAudio(@NotNull Identifier location) {
		return null;
	}

	@Nullable
	@Override
	public CompletableFuture<Audio> getAudioAsync(@NotNull Identifier location) {
		return null;
	}

	@Override
	public boolean isLoaded(@NotNull Identifier location) {
		return false;
	}

	@Override
	public void unload(@NotNull Identifier location) {

	}

	@Override
	public void unloadAll() {

	}

	@NotNull
	@Unmodifiable
	@Override
	public Set<Identifier> getLoadedAudios() {
		return Set.of();
	}

	@Override
	public void preload(@NotNull Identifier location) {

	}

	@Override
	public void awaitPreload() throws InterruptedException {

	}

	@Override
	public void awaitPreload(long timeout, @NotNull TimeUnit unit) throws InterruptedException {

	}

	@Override
	public void close() {
		this.decoderThreadPool.shutdown();
		try {
			if (!this.decoderThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				this.decoderThreadPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			this.decoderThreadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private static class AudioDecoderThreadFactory implements ThreadFactory {
		private final AtomicInteger id;

		public AudioDecoderThreadFactory() {
			this.id = new AtomicInteger(0);
		}

		@NotNull
		@Override
		public Thread newThread(@NotNull Runnable r) {
			Objects.requireNonNull(r, "r is null");

			Thread thread = new Thread(r);
			thread.setDaemon(false);
			thread.setName("AudioDecoder-" + this.id.getAndIncrement());
			return thread;
		}
	}
}
