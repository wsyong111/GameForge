package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DefaultAudioManager implements AudioManager, AutoCloseable {
	private static final Logger LOGGER = Log.getLogger();

	private final ResourceProvider resourceProvider;
	private final AudioDecoderPool decoderPool;

	private final AudioDecoderRegistry decoderMap;

	private final Map<Identifier, Future<Audio>> pendingAudio;
	private final Map<Identifier, Audio> audioCache;

	public DefaultAudioManager(@NotNull ResourceProvider resourceProvider) {
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");

		this.resourceProvider = resourceProvider;

		int decoderThreadCount = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors() - 2, 4));
		this.decoderPool = new AudioDecoderPool(
			128,
			decoderThreadCount,
			10,
			TimeUnit.SECONDS
		);

		this.decoderMap = new AudioDecoderRegistry();

		this.pendingAudio = new ConcurrentHashMap<>();
		this.audioCache = new ConcurrentHashMap<>();
	}

	@Nullable
	@Override
	public Audio getAudio(@NotNull Identifier location) {
		Objects.requireNonNull(location, "location is null");

		Audio cachedAudio = this.audioCache.get(location);
		if (cachedAudio != null)
			return cachedAudio;

		Resource resource = this.resourceProvider.getResource(location);
		if (resource == null)
			return null;

		MimeType mimeType = resource.getType();
		List<AudioDecoderRegistry.FactoryInfo> factoryInfos = this.decoderMap.get(mimeType, Set.of(AudioDecodeHint.PRELOAD_METADATA));
		if (factoryInfos.isEmpty()) {
			LOGGER.debug("No audio decoder compatible with {} found \"{}\"", mimeType, location);
			return null;
		}

		LOGGER.debug("Found {} audio decoder from \"{}\" [{}]", factoryInfos.size(), location, mimeType);

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
}
