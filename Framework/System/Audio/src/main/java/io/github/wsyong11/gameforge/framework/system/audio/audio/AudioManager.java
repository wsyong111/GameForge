package io.github.wsyong11.gameforge.framework.system.audio.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.concurrent.TimeUnit;

// TODO: 2026/3/24 完整实现，流式解码
public interface AudioManager {
	@Nullable
	Audio getAudio(@NotNull Identifier location);

	boolean isLoaded(@NotNull Identifier location);

	void unload(@NotNull Identifier location);

	void unloadAll();

	@NotNull
	@Unmodifiable
	Set<Identifier> getLoadedAudios();

	void preload(@NotNull Identifier location);

	void awaitPreload() throws InterruptedException;

	void awaitPreload(long timeout, @NotNull TimeUnit unit) throws InterruptedException;

	boolean registerAudioDecoder(@NotNull AudioDecoderFactory factory, int priority);

	boolean unregisterAudioDecoder(@NotNull AudioDecoderFactory factory);
}
