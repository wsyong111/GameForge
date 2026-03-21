package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio.decode;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import io.github.wsyong11.gameforge.util.io.SeekableInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.className;

public class AudioDecodeManager implements AutoCloseable {
	private static final Logger LOGGER = Log.getLogger();

	private final TaskHandler audioTaskHandler;
	private final AudioDecoderRegistry registry;

	private final ExecutorService pool;
	private final List<DecodeContext> contextList;
//	private final Map<DecodeContext, Future<?>> contextFutureMap;

	private volatile boolean closed;

	public AudioDecodeManager(@NotNull TaskHandler audioTaskHandler, @NotNull AudioDecoderRegistry registry) {
		Objects.requireNonNull(audioTaskHandler, "audioTaskHandler is null");
		Objects.requireNonNull(registry, "registry is null");

		this.audioTaskHandler = audioTaskHandler;
		this.registry = registry;

		this.pool = new ThreadPoolExecutor(
			1,
			4,
			10,
			TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(256)
//			new LimitedCapacityBlockingQueue<>(new PriorityBlockingQueue<>(), 256)
		);

//		this.contextFutureMap = new IdentityHashMap<>();

		this.closed = false;
		contextList = new ArrayList<>();
	}

	protected void ensureState() {
		if (this.closed)
			throw new IllegalStateException("Audio decode manager is closed");
	}

	@Nullable
	protected DecodeContext createDecodeContext(
		int priority,
		@NotNull Identifier identifier,
		@NotNull Resource resource,
		@NotNull Set<AudioDecodeHint> hints
	) {
		Objects.requireNonNull(identifier, "identifier is null");
		Objects.requireNonNull(resource, "resource is null");
		Objects.requireNonNull(hints, "hints is null");

		MimeType mimeType = resource.getType();
		List<AudioDecoderRegistry.FactoryInfo> factoryInfos = this.registry.get(mimeType, hints);
		if (factoryInfos.isEmpty()) {
			LOGGER.debug("No audio decoder compatible with \"{}\" [{}]", identifier, mimeType);
			return null;
		}

		LOGGER.debug("Found {} available audio decoders with \"{}\" [{}]", factoryInfos.size(), identifier, mimeType);

		InputStream stream;
		try {
			stream = resource.openStream();
		} catch (IOException e) {
			LOGGER.warn("Failed open resource stream {}", identifier, e);
			return null;
		}

		SeekableInputStream seekableStream = new SeekableInputStream(stream);

		AudioDecoder selectedDecoder = null;
		for (AudioDecoderRegistry.FactoryInfo info : factoryInfos) {
			AudioDecoderFactory factory = info.getFactory();

			try (SeekableInputStream duplicateStream = seekableStream.duplicate()) {
				if (!factory.checkMagic(duplicateStream))
					continue;
			} catch (Throwable e) {
				LOGGER.error("Uncaught exception in checking magic, factory={}", className(factory), e);
				continue;
			}

			AudioDecoder decoder;
			try {
				//noinspection resource
				decoder = factory.create(new DecoderInfoImpl(seekableStream, hints, mimeType));
			} catch (Throwable e) {
				LOGGER.error("Failed to create decoder, factory={}", className(factory), e);
				continue;
			}

			selectedDecoder = decoder;
			break;
		}

		if (selectedDecoder == null) {
			LOGGER.warn("No available decoder found from \"{}\" [{}]", factoryInfos.size(), identifier, mimeType);
			return null;
		}

		return new DecodeContext(
			this.pool,
			this.audioTaskHandler,
			identifier,
			selectedDecoder,
			priority,
			seekableStream
		);
	}

	@Nullable
	public Audio decode(int priority, @NotNull Identifier identifier, @NotNull Resource resource, @NotNull Set<AudioDecodeHint> hints) {
		Objects.requireNonNull(identifier, "identifier is null");
		Objects.requireNonNull(resource, "resource is null");
		Objects.requireNonNull(hints, "hints is null");

		this.ensureState();

		DecodeContext context = this.createDecodeContext(priority, identifier, resource, hints);
		if (context == null)
			return null;

		synchronized (this.contextList) {
			this.contextList.add(context);
		}

		context.preload();

		return new DecodeContextAudio(context);
	}

	@Override
	public void close() {
		if (this.closed)
			return;
		this.closed = true;

		List<DecodeContext> contexts;
		synchronized (this.contextList) {
			contexts = List.copyOf(this.contextList);
			this.contextList.clear();
		}

		for (DecodeContext context : contexts)
			context.close();

		this.pool.shutdown();
		try {
			if (!this.pool.awaitTermination(10, TimeUnit.SECONDS))
				this.pool.shutdownNow();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static class DecoderInfoImpl implements AudioDecoder.DecodeInfo {
		private final SeekableInputStream stream;
		private final Set<AudioDecodeHint> hints;
		private final MimeType mime;

		private DecoderInfoImpl(@NotNull SeekableInputStream stream, @NotNull Set<AudioDecodeHint> hints, @NotNull MimeType mime) {
			Objects.requireNonNull(stream, "stream is null");
			Objects.requireNonNull(hints, "hints is null");
			Objects.requireNonNull(mime, "mime is null");

			this.stream = stream;
			this.hints = Set.copyOf(hints);
			this.mime = mime;
		}

		@NotNull
		@Override
		public InputStream openStream() {
			return this.stream.duplicate();
		}

		@NotNull
		@UnmodifiableView
		@Override
		public Set<AudioDecodeHint> getHints() {
			return this.hints;
		}

		@NotNull
		@Override
		public MimeType getMime() {
			return this.mime;
		}
	}
}

/*
AudioManager
|- AudioDecodeManager
|  |- AudioDecodeThread (Thread)
|  |- AudioDecodePool
|     |- AudioDecodeTask (Thread)
|- Audio
 */