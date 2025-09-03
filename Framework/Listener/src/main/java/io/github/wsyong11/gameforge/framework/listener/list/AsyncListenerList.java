package io.github.wsyong11.gameforge.framework.listener.list;

import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class AsyncListenerList extends AbstractListenerList {
	private static final Logger LOGGER = Log.getLogger();

	private final Executor executor;

	public AsyncListenerList(@NotNull Executor executor) {
		Objects.requireNonNull(executor, "executor is null");
		this.executor = executor;
	}

	@Override
	protected <T extends IListener> void fire(@NotNull Class<T> type, @NotNull List<T> listeners, @NotNull Consumer<T> trigger, @NotNull ListenerExceptionCallback<T> exceptionCallback) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(listeners, "listeners is null");
		Objects.requireNonNull(trigger, "trigger is null");
		Objects.requireNonNull(exceptionCallback, "exceptionCallback is null");

		LOGGER.trace("Fire listener {}", lazy(type::getName));

		for (T listener : listeners) {
			try {
				this.executor.execute(() -> trigger.accept(listener));
			} catch (Throwable e) {
				exceptionCallback.onListenerError(listener, e);
			}
		}
	}
}
