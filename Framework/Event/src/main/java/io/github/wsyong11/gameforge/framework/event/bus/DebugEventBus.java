package io.github.wsyong11.gameforge.framework.event.bus;

import io.github.wsyong11.gameforge.framework.env.EnvConfig;
import io.github.wsyong11.gameforge.framework.event.Event;
import io.github.wsyong11.gameforge.framework.event.EventBus;
import io.github.wsyong11.gameforge.framework.event.ex.EventListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DebugEventBus extends EventBusWrapper {
	private static final Logger LOGGER = Log.getLogger();

	@NotNull
	public static EventBus wrap(@NotNull EventBus delegate, @NotNull String debugName) {
		Objects.requireNonNull(delegate, "delegate is null");
		Objects.requireNonNull(debugName, "debugName is null");
		return wrap(delegate, debugName, false);
	}

	@NotNull
	public static EventBus wrap(@NotNull EventBus delegate, @NotNull String debugName, boolean ignoreConfig) {
		Objects.requireNonNull(delegate, "delegate is null");
		Objects.requireNonNull(debugName, "debugName is null");

		if (delegate instanceof DebugEventBus debugEventBus)
			return debugEventBus;

		return ignoreConfig || EnvConfig.DEBUG.getValue()
			? new DebugEventBus(delegate, debugName)
			: delegate;
	}

	private final String debugName;

	public DebugEventBus(@NotNull EventBus delegate, @NotNull String debugName) {
		super(delegate);
		Objects.requireNonNull(delegate, "delegate is null");
		Objects.requireNonNull(debugName, "debugName is null");

		this.debugName = debugName;
	}

	@NotNull
	public String getDebugName() {
		return this.debugName;
	}

	@Override
	public <T extends Event> boolean post(@NotNull T event) {
		LOGGER.trace("Post event in bus {}: {}", this.debugName, event);
		return super.post(event);
	}

	@Override
	public <T extends Event> boolean post(@NotNull T event, @NotNull EventListenerExceptionCallback exceptionCallback) {
		LOGGER.trace("Post event in bus {}: {}", this.debugName, event);
		return super.post(event, exceptionCallback);
	}

	@NotNull
	@Override
	public <T extends Event> Future<Boolean> postAsync(@NotNull ExecutorService executor, @NotNull T event) {
		LOGGER.trace("Post async event in bus {}, executor {}: {}", this.debugName, executor, event);
		return super.postAsync(executor, event);
	}

	@NotNull
	@Override
	public <T extends Event> Future<Boolean> postAsync(@NotNull ExecutorService executor, @NotNull T event, @NotNull EventListenerExceptionCallback exceptionCallback) {
		LOGGER.trace("Post async event in bus {}, executor {}: {}", this.debugName, executor, event);
		return super.postAsync(executor, event, exceptionCallback);
	}
}
