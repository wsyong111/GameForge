package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio.decode.AudioDecodeManager;
import io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio.decode.AudioDecoderRegistry;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultAudioManager implements AudioManager, AutoCloseable {
	private static final Logger LOGGER = Log.getLogger();

	private final ResourceProvider resourceProvider;

	private final AudioDecoderRegistry decoderMap;
	private final AudioDecodeManager decodeManager;

	private final Map<Identifier, Audio> audioCache;

	public DefaultAudioManager(@NotNull ResourceProvider resourceProvider, @NotNull TaskHandler taskHandler) {
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");
		Objects.requireNonNull(taskHandler, "taskHandler is null");

		this.resourceProvider = resourceProvider;

//		int decoderThreadCount = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors() - 2, 4));

//			taskHandler,
//			128,
//			decoderThreadCount,
//			10,
//			TimeUnit.SECONDS

		this.decoderMap = new AudioDecoderRegistry();
		this.decodeManager = new AudioDecodeManager(taskHandler, this.decoderMap);

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

		Audio audio = this.decodeManager.decode(0, location, resource, Set.of(AudioDecodeHint.PRELOAD_METADATA));
		this.audioCache.put(location, audio);

		return audio;
	}

	@Override
	public boolean isLoaded(@NotNull Identifier location) {
		Objects.requireNonNull(location, "location is null");
		return this.audioCache.containsKey(location);
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
		return Set.copyOf(this.audioCache.keySet());
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
		for (Audio audio : this.audioCache.values())
			audio.close();
		this.audioCache.clear();

		this.decodeManager.close();
		this.decoderMap.clear();
	}
}
