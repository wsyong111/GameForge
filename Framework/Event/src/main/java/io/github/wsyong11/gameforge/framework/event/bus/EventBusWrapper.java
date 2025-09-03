package io.github.wsyong11.gameforge.framework.event.bus;

import io.github.wsyong11.gameforge.framework.event.Event;
import io.github.wsyong11.gameforge.framework.event.EventBus;
import io.github.wsyong11.gameforge.framework.event.EventPriority;
import io.github.wsyong11.gameforge.framework.event.IEventListener;
import io.github.wsyong11.gameforge.framework.event.ex.EventListenerExceptionCallback;
import io.github.wsyong11.gameforge.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class EventBusWrapper extends Wrapper<EventBus> implements EventBus {
	public EventBusWrapper() { /* no-op */ }

	public EventBusWrapper(@Nullable EventBus delegate) {
		super(delegate);
	}

	@Override
	public void unregisterAll() {
		this.delegate().unregisterAll();
	}

	@Override
	public <T extends Event> boolean post(@NotNull T event) {
		return this.delegate().post(event);
	}

	@Override
	public <T extends Event> boolean post(@NotNull T event, @NotNull EventListenerExceptionCallback exceptionCallback) {
		return this.delegate().post(event, exceptionCallback);
	}

	@NotNull
	@Override
	public <T extends Event> Future<Boolean> postAsync(@NotNull ExecutorService executor, @NotNull T event) {
		return this.delegate().postAsync(executor, event);
	}

	@NotNull
	@Override
	public <T extends Event> Future<Boolean> postAsync(@NotNull ExecutorService executor, @NotNull T event, @NotNull EventListenerExceptionCallback exceptionCallback) {
		return this.delegate().postAsync(executor, event, exceptionCallback);
	}

	@Override
	public <T extends Event> void register(@NotNull Class<T> type, @NotNull EventPriority priority, @NotNull IEventListener<? super T> listener) {
		this.delegate().register(type, priority, listener);
	}

	@Override
	public void register(@NotNull Object obj) {
		this.delegate().register(obj);
	}

	@Override
	public void register(@NotNull Class<?> obj) {
		this.delegate().register(obj);
	}

	@Override
	public <T extends Event> void unregister(@NotNull IEventListener<T> listener) {
		this.delegate().unregister(listener);
	}

	@Override
	public void unregister(@NotNull Object obj) {
		this.delegate().unregister(obj);
	}

	@Override
	public void unregister(@NotNull Class<?> obj) {
		this.delegate().unregister(obj);
	}

	@Override
	public <T extends Event> void register(@NotNull Class<T> type, @NotNull IEventListener<T> listener) {
		this.delegate().register(type, listener);
	}

	@Override
	public <T extends Event> void register(@NotNull IEventListener<T> listener) {
		this.delegate().register(listener);
	}

	@Override
	public <T extends Event> void register(@NotNull EventPriority priority, @NotNull IEventListener<T> listener) {
		this.delegate().register(priority, listener);
	}
}
