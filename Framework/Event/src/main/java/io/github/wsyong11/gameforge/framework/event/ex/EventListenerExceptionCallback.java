package io.github.wsyong11.gameforge.framework.event.ex;

import io.github.wsyong11.gameforge.framework.event.IEventListener;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@FunctionalInterface
public interface EventListenerExceptionCallback {
	@NotNull
	static EventListenerExceptionCallback log(@NotNull Logger logger) {
		Objects.requireNonNull(logger, "logger is null");
		return (listener, error) ->
			logger.error("An exception occurred when running the event listener {}", listener, error);
	}

	void onException(@NotNull IEventListener<?> listener, @NotNull Throwable exception);
}
