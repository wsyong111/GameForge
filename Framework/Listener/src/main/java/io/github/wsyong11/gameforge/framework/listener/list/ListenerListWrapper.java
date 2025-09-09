package io.github.wsyong11.gameforge.framework.listener.list;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerException;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ListenerListWrapper extends Wrapper<ListenerList> implements ListenerList {
	public ListenerListWrapper() { /* no-op */ }

	public ListenerListWrapper(@Nullable ListenerList delegate) {
		super(delegate);
	}

	@Override
	public <T extends IListener> void fire(@NotNull Class<T> type, @NotNull Consumer<T> trigger, @NotNull ListenerExceptionCallback<T> exceptionCallback) {
		this.delegate().fire(type, trigger, exceptionCallback);
	}

	@Override
	public <T extends IListener> void fire(@NotNull Class<T> type, @NotNull Consumer<T> trigger) throws ListenerException {
		this.delegate().fire(type, trigger);
	}

	@Override
	public int size(@NotNull Class<? extends IListener> type) {
		return this.delegate().size(type);
	}

	@Override
	public int size() {
		return this.delegate().size();
	}

	@Override
	public void clear(@NotNull Class<? extends IListener> type) {
		this.delegate().clear(type);
	}

	@Override
	public void clear() {
		this.delegate().clear();
	}

	@Override
	public <T extends IListener> void add(@NotNull Class<T> type, @NotNull T listener) {
		this.delegate().add(type, listener);
	}

	@Override
	public <T extends IListener> void remove(@NotNull Class<T> type, @NotNull T listener) {
		this.delegate().remove(type, listener);
	}
}
