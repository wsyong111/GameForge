package io.github.wsyong11.gameforge.framework.system.audio.impl.simple;

import io.github.wsyong11.gameforge.framework.system.audio.engine.AudioEngine;
import io.github.wsyong11.gameforge.framework.system.audio.engine.AudioEngineContext;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import io.github.wsyong11.gameforge.util.concurrent.executor.TaskQueueExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractAudioEngine implements AudioEngine {
	protected final AudioEngineContext context;

	private final TaskQueueExecutor taskQueue;
	protected final TaskHandler taskHandler;

	public AbstractAudioEngine(@NotNull AudioEngineContext ctx) {
		Objects.requireNonNull(ctx, "ctx is null");

		this.context = ctx;

		this.taskQueue = new TaskQueueExecutor();
		this.taskHandler = new TaskHandler(this.taskQueue);
	}

	@Override
	public void loopTick() {
		this.taskQueue.run(this::processTaskException);
	}

	protected void processTaskException(@NotNull Throwable e) {

	}

	@Override
	public void close() {
		this.taskQueue.clear();
	}
}
