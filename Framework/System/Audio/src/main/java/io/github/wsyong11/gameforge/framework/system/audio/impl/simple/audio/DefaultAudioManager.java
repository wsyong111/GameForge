package io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultAudioManager implements AudioManager, AutoCloseable {
	private final ResourceProvider resourceProvider;
	private final AudioDecoderPool decoderPool;

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

		this.audioCache = new ConcurrentHashMap<>();
	}

	@Nullable
	@Override
	public Audio getAudio(@NotNull Identifier location) {
		Resource resource = this.resourceProvider.getResource(location);

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
		this.decoderPool.shutdown();
	}
}
