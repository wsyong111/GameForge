package io.github.wsyong11.gameforge.framework.listener.list;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SyncListenerList extends AbstractListenerList {
	@Override
	protected <T extends IListener> void fire(@NotNull Class<T> type, @NotNull List<T> listeners, @NotNull Consumer<T> trigger, @NotNull ListenerExceptionCallback<T> exceptionCallback) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(listeners, "listeners is null");
		Objects.requireNonNull(trigger, "trigger is null");
		Objects.requireNonNull(exceptionCallback, "exceptionCallback is null");

		for (T listener : listeners) {
			try {
				trigger.accept(listener);
			} catch (Throwable e) {
				exceptionCallback.onListenerError(listener, e);
			}
		}
	}
}
