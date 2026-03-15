package io.github.wsyong11.gameforge.framework.system.audio;

import io.github.wsyong11.gameforge.framework.system.audio.engine.AudioEngine;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioDeviceException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.Ticker;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import io.github.wsyong11.gameforge.util.concurrent.executor.TaskQueueExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class AudioThread extends Thread {
	private static final Logger LOGGER = Log.getLogger();

	private final AudioEngine engine;
	private final TaskQueueExecutor taskExecutor;
	private final TaskHandler taskHandler;
	private final Ticker mainLoopTicker;

	public AudioThread(@NotNull AudioEngine engine) {
		Objects.requireNonNull(engine, "engine is null");

		this.engine = engine;
		this.taskExecutor = new TaskQueueExecutor();
		this.taskHandler = new TaskHandler(this.taskExecutor);
		this.mainLoopTicker = Ticker.accumulator(engine.getLoopPreSec(), this::tick);

		this.setName("AudioThread");
		this.setDaemon(true);
	}

	@NotNull
	public TaskHandler getTaskHandler() {
		return this.taskHandler;
	}

	@Override
	public void run() {
		LOGGER.debug("Audio thread init...");
		this.engine.init();

		AudioDeviceIdentity defaultDevice = this.engine.getDefaultDevice();
		try {
			this.engine.setActiveDevice(defaultDevice);
		} catch (AudioDeviceException e) {
			this.engine.setActiveDevice(AudioDeviceIdentity.empty());
			LOGGER.warn("Cannot set the output device to {}, Set the empty device as placeholder", defaultDevice, e);
		}

		LOGGER.debug("Audio thread init completed");
		try {
			this.mainLoopTicker.loop();
		} catch (Exception e) {
			LOGGER.error("Exception when looping audio engine", e);
		} finally {
			try {
				this.engine.close();
			} catch (Exception e) {
				LOGGER.error("Exception when closing audio engine", e);
			}

			LOGGER.debug("Audio thread stopped");
		}
	}

	private void tick(double dtMs) {
		this.taskExecutor.run(exception ->
			LOGGER.error("An exception occurred during running a task", exception));

		this.engine.loopTick();
		this.mainLoopTicker.setTickSpeed(this.engine.getLoopPreSec());
	}

	public void shutdown() {
		this.mainLoopTicker.stop();
		this.taskExecutor.clear();
		try {
			this.join(2000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
