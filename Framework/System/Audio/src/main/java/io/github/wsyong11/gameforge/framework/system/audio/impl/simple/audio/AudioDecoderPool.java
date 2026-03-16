package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.concurrent.LimitedCapacityBlockingQueue;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import io.github.wsyong11.gameforge.util.io.SeekableInputStream;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class AudioDecoderPool {
	private static final Logger LOGGER = Log.getLogger();

	private final ExecutorService executor;
	private final TaskHandler audioTaskHandler;

	public AudioDecoderPool(
		@NotNull TaskHandler audioTaskHandler,
		int maxCapacity,
		int maxPoolSize,
		long keepAliveTime,
		@NotNull TimeUnit unit
	) {
		Objects.requireNonNull(unit, "unit is null");
		Objects.requireNonNull(audioTaskHandler, "audioTaskHandler is null");

		this.audioTaskHandler = audioTaskHandler;

		this.executor = new ThreadPoolExecutor(
			1,
			maxPoolSize,
			keepAliveTime,
			unit,
			new LimitedCapacityBlockingQueue<>(new PriorityBlockingQueue<>(maxCapacity), maxCapacity),
			new AudioDecoderThreadFactory(),
			new ThreadPoolExecutor.CallerRunsPolicy()
		);
	}

	@NotNull
	public Audio decode(
		@NotNull Identifier location,
		int priority,
		@NotNull AudioDecoder decoder,
		@NotNull SeekableInputStream stream
	) {
		Objects.requireNonNull(location, "location is null");
		Objects.requireNonNull(decoder, "decoder is null");
		Objects.requireNonNull(stream, "stream is null");

		DecodedAudio audio = new DecodedAudio(location, this.audioTaskHandler);
		DecoderTask task = new DecoderTask(priority, decoder, audio);
		this.executor.submit(task);

		return audio;
	}

	public void shutdown() {
		this.executor.shutdown();
		try {
			if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
				this.executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			this.executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	protected class DecoderTask implements Callable<Boolean>, Comparable<DecoderTask> {
		private final int priority;
		private final AudioDecoder decoder;
		private final DecodedAudio audio;

		private DecoderTask(int priority, @NotNull AudioDecoder decoder, @NotNull DecodedAudio audio) {
			Objects.requireNonNull(decoder, "decoder is null");
			Objects.requireNonNull(audio, "audio is null");

			this.priority = priority;
			this.decoder = decoder;
			this.audio = audio;
		}

		@Override
		public int compareTo(@NotNull DecoderTask task) {
			Objects.requireNonNull(task, "task is null");
			return 0;
		}

		@NotNull
		@Override
		public Boolean call() {
			this.decoder.preload();

			Identifier location = this.audio.getLocation();

			AudioMetadata metadata;
			try {
				metadata = this.decoder.getMetadata();
			} catch (AudioDecodeException e) {
				LOGGER.error("Cannot decode audio metadata {}", location, e);
				this.audio.setStatusFailed();
				return false;
			}

			LOGGER.trace("[{}] Decoded metadata: {}", location, lazy(metadata));

			this.audio.setStatusReady(new DecodedAudio.Provider() {
				@NotNull
				@Override
				public AudioMetadata getMetadata() {
					return metadata;
				}

				@NotNull
				@Override
				public AudioStream newStream() {
					return null;
				}

				@Override
				public void close() {

				}
			});

			return true;
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

/*
播放音频:
	1. 如果音频已加载
	1.1. 使用缓存并返回音频对象
	2. 返回音频对象的同时在后台加载
	3. 如果音频未加载完成则等待直到超时
	4. 否则播放音频
 */