package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import io.github.wsyong11.gameforge.util.io.SeekableInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.className;

public class DefaultAudioManager implements AudioManager, AutoCloseable {
	private static final Logger LOGGER = Log.getLogger();

	private final ResourceProvider resourceProvider;

	private final AudioDecoderPool decoderPool;

	private final AudioDecoderRegistry decoderMap;

	private final Map<Identifier, Audio> audioCache;

	public DefaultAudioManager(@NotNull ResourceProvider resourceProvider, @NotNull TaskHandler taskHandler) {
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");
		Objects.requireNonNull(taskHandler, "taskHandler is null");

		this.resourceProvider = resourceProvider;

		int decoderThreadCount = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors() - 2, 4));
		this.decoderPool = new AudioDecoderPool(
			taskHandler,
			128,
			decoderThreadCount,
			10,
			TimeUnit.SECONDS
		);

		this.decoderMap = new AudioDecoderRegistry();

		this.audioCache = new ConcurrentHashMap<>();
	}

	@Nullable
	@Override
	public Audio getAudio(@NotNull Identifier location) {
		Objects.requireNonNull(location, "location is null");

		Audio cachedAudio = this.audioCache.get(location);
		if (cachedAudio != null)
			return cachedAudio;

		return this.decode(0, location, Set.of(AudioDecodeHint.PRELOAD_METADATA));
	}

	@Nullable
	private Audio decode(int priority, @NotNull Identifier location, @NotNull Set<AudioDecodeHint> hints) {
		Objects.requireNonNull(location, "location is null");
		Objects.requireNonNull(hints, "hints is null");

		Resource resource = this.resourceProvider.getResource(location);
		if (resource == null) {
//			LOGGER.warn("Audio resource not found {}", location);
			return null;
		}

		MimeType mimeType = resource.getType();
		List<AudioDecoderRegistry.FactoryInfo> factoryInfos = this.decoderMap.get(mimeType, hints);
		if (factoryInfos.isEmpty()) {
			LOGGER.debug("No audio decoder compatible with \"{}\" [{}]", location, mimeType);
			return null;
		}

		LOGGER.debug("Found {} available audio decoders with \"{}\" [{}]", factoryInfos.size(), location, mimeType);

		InputStream stream;
		try {
			stream = resource.openStream();
		} catch (IOException e) {
			LOGGER.warn("Failed open resource stream {}", location, e);
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
			LOGGER.warn("No available decoder found from \"{}\" [{}]", factoryInfos.size(), location, mimeType);
			return null;
		}

		return this.decoderPool.decode(priority, selectedDecoder, seekableStream);
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
	public boolean registerAudioDecoder(@NotNull AudioDecoderFactory factory, int priority) {
		Objects.requireNonNull(factory, "factory is null");
		return this.decoderMap.register(factory, priority);
	}

	@Override
	public boolean unregisterAudioDecoder(@NotNull AudioDecoderFactory factory) {
		Objects.requireNonNull(factory, "factory is null");
		return this.decoderMap.unregister(factory);
	}

	@Override
	public void close() {
		this.decoderPool.shutdown();
		this.decoderMap.clear();
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
