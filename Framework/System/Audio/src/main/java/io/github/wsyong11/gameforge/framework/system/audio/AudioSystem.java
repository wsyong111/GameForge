package io.github.wsyong11.gameforge.framework.system.audio;

import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import io.github.wsyong11.gameforge.framework.system.audio.engine.AudioEngine;
import io.github.wsyong11.gameforge.framework.system.audio.engine.AudioEngineContext;
import io.github.wsyong11.gameforge.framework.system.audio.provider.AudioEngineProvider;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class AudioSystem {
	private static final Logger LOGGER = Log.getLogger();

	private static volatile AudioSystem INSTANCE = null;

	@NotNull
	public static AudioSystem init(@NotNull AudioEngineProvider engineProvider, @NotNull ResourceProvider resourceProvider) {
		Objects.requireNonNull(engineProvider, "engineProvider is null");
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");

		AudioSystem globalInstance = getInstanceSafe();
		if (globalInstance != null)
			return globalInstance;

		AudioSystem instance;

		synchronized (AudioSystem.class) {
			LOGGER.debug("Initialize audio system");

			instance = new AudioSystem(engineProvider, resourceProvider);
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

	private final AudioManager audioManager;

	private volatile boolean closed;

	private AudioSystem(@NotNull AudioEngineProvider engineProvider, @NotNull ResourceProvider resourceProvider) {
		Objects.requireNonNull(engineProvider, "engineProvider is null");
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");

		this.thread = new AudioThread();

		Context context = new Context(resourceProvider, this.thread.getTaskHandler());
		this.engine = engineProvider.getFactory().apply(context);

		this.thread.setEngine(this.engine);

		this.audioManager = this.engine.getAudioManager();

		this.thread.start();
	}

	@NotNull
	public AudioListener getListener() {
		this.checkState();
		return this.engine.getListener();
	}

	@NotNull
	public AudioManager getAudioManager() {
		this.checkState();
		return this.audioManager;
	}

	private void checkState() {
		if (this.closed)
			throw new IllegalStateException("Audio system closed");
	}

	private synchronized void shutdownThis() {
		if (this.closed)
			return;
		this.closed = true;

		this.thread.shutdown();
	}

	private static class Context implements AudioEngineContext {
		private final ResourceProvider resourceProvider;
		private final TaskHandler audioTaskHandler;

		private Context(@NotNull ResourceProvider resourceProvider, @NotNull TaskHandler audioTaskHandler) {
			Objects.requireNonNull(resourceProvider, "resourceProvider is null");
			Objects.requireNonNull(audioTaskHandler, "audioTaskHandler is null");

			this.resourceProvider = resourceProvider;
			this.audioTaskHandler = audioTaskHandler;
		}

		@NotNull
		@Override
		public ResourceProvider getResourceProvider() {
			return this.resourceProvider;
		}

		@NotNull
		@Override
		public TaskHandler getAudioTaskHandler() {
			return this.audioTaskHandler;
		}
	}
}
