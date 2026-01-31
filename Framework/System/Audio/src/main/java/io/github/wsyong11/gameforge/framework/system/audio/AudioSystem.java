package io.github.wsyong11.gameforge.framework.system.audio;

import io.github.wsyong11.gameforge.framework.system.audio.provider.AudioEngineProvider;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class AudioSystem {
	private static final Logger LOGGER = Log.getLogger();

	private static volatile AudioSystem INSTANCE = null;

	@NotNull
	public static AudioSystem init(@NotNull AudioEngineProvider engineProvider) {
		Objects.requireNonNull(engineProvider, "engineProvider is null");

		AudioSystem globalInstance = getInstanceSafe();
		if (globalInstance != null)
			return globalInstance;

		AudioSystem instance;

		synchronized (AudioSystem.class) {
			LOGGER.debug("Initialize audio system");

			instance = new AudioSystem(engineProvider);
			INSTANCE = instance;
		}

		return instance;
	}

	public static void shutdown() {
		AudioSystem instance = getInstanceSafe();
		if (instance == null)
			return;

		LOGGER.debug("Shutdown audio system");
		instance.shutdownThis();

		synchronized (AudioSystem.class) {
			INSTANCE = null;
		}
	}

	@Nullable
	public static AudioSystem getInstanceSafe() {
		if (INSTANCE == null) {
			synchronized (AudioSystem.class) {
				return INSTANCE;
			}
		}

		return INSTANCE;
	}

	@NotNull
	public static AudioSystem getInstance() {
		AudioSystem instance = getInstanceSafe();
		if (instance == null)
			throw new IllegalThreadStateException("Audio system doesn't initialize");
		return instance;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final AudioEngine engine;
	private final AudioThread thread;

	private AudioSystem(@NotNull AudioEngineProvider engineProvider) {
		Objects.requireNonNull(engineProvider, "engineProvider is null");

		this.engine = engineProvider.getFactory().get();
		this.thread = new AudioThread(this.engine);

		this.thread.start();
	}

	private void shutdownThis() {
		this.thread.shutdown();
	}
}
