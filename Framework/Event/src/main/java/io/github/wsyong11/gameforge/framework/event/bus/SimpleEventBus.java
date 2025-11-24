package io.github.wsyong11.gameforge.framework.event.bus;

import io.github.wsyong11.gameforge.framework.event.CancellableEvent;
import io.github.wsyong11.gameforge.framework.event.Event;
import io.github.wsyong11.gameforge.framework.event.IEventListener;
import io.github.wsyong11.gameforge.framework.event.ex.EventDispatchException;
import io.github.wsyong11.gameforge.framework.event.ex.EventListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.event.ex.ListenerException;
import io.github.wsyong11.gameforge.framework.event.reflect.ReflectEventBus;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class SimpleEventBus extends ReflectEventBus {
	private <T extends Event> boolean postInternal(@NotNull T event, @NotNull EventListenerExceptionCallback exceptionCallback) {
		Objects.requireNonNull(event, "event is null");
		Objects.requireNonNull(exceptionCallback, "exceptionCallback is null");

		@SuppressWarnings("unchecked")
		Class<T> eventType = (Class<T>) event.getClass();
		List<AbstractEventBus.ListenerItem<T>> listeners = this.getCachedListeners(eventType);

		for (AbstractEventBus.ListenerItem<T> listenerItem : listeners) {
			IEventListener<? super T> listener = listenerItem.getListener();
			try {
				listener.onEvent(event);
			} catch (Throwable e) {
				exceptionCallback.onException(listener, e);
			}
		}
		return !CancellableEvent.isCanceled(event);
	}

	@Override
	public <T extends Event> boolean post(@NotNull T event) throws EventDispatchException {
		Objects.requireNonNull(event, "event is null");

		ExceptionHandler exceptionHandler = new ExceptionHandler();
		boolean result = this.postInternal(event, (listener, exception) ->
			exceptionHandler.accept(exception));

		exceptionHandler.throwException("An exception occurred while running listener", EventDispatchException::new);

		return result;
	}

	@Override
	public <T extends Event> boolean post(@NotNull T event, @NotNull EventListenerExceptionCallback exceptionCallback) {
		Objects.requireNonNull(event, "event is null");
		Objects.requireNonNull(exceptionCallback, "exceptionCallback is null");
		return this.postInternal(event, exceptionCallback);
	}

	@NotNull
	@Override
	public <T extends Event> Future<Boolean> postAsync(@NotNull ExecutorService executor, @NotNull T event) {
		Objects.requireNonNull(executor, "executor is null");
		Objects.requireNonNull(event, "event is null");

		@SuppressWarnings("unchecked")
		Class<T> eventType = (Class<T>) event.getClass();
		List<AbstractEventBus.ListenerItem<T>> listeners = this.getCachedListeners(eventType);

		return new PostFuture(event, listeners
			.stream()
			.map(AbstractEventBus.ListenerItem::getListener)
			.map(listener -> (Runnable) () -> listener.onEvent(event))
			.<Future<?>>map(executor::submit)
			.toList());
	}

	@NotNull
	@Override
	public <T extends Event> Future<Boolean> postAsync(@NotNull ExecutorService executor, @NotNull T event, @NotNull EventListenerExceptionCallback exceptionCallback) {
		Objects.requireNonNull(executor, "executor is null");
		Objects.requireNonNull(event, "event is null");
		Objects.requireNonNull(exceptionCallback, "exceptionCallback is null");

		@SuppressWarnings("unchecked")
		Class<T> eventType = (Class<T>) event.getClass();
		List<AbstractEventBus.ListenerItem<T>> listeners = this.getCachedListeners(eventType);

		return new PostFuture(event, listeners
			.stream()
			.map(AbstractEventBus.ListenerItem::getListener)
			.map(listener -> (Runnable) () -> {
				try {
					listener.onEvent(event);
				} catch (Throwable e) {
					exceptionCallback.onException(listener, e);
				}
			})
			.<Future<?>>map(executor::submit)
			.toList());
	}

	private static class PostFuture implements Future<Boolean> {
		private final Event event;
		private final List<Future<?>> tasks;

		private PostFuture(@NotNull Event event, @NotNull List<Future<?>> tasks) {
			Objects.requireNonNull(event, "event is null");
			Objects.requireNonNull(tasks, "tasks is null");
			this.event = event;
			this.tasks = Collections.unmodifiableList(tasks);
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			boolean cancelled = true;
			for (Future<?> task : this.tasks)
				cancelled &= task.cancel(mayInterruptIfRunning);
			return cancelled;
		}

		@Override
		public boolean isCancelled() {
			return this.tasks.stream().allMatch(Future::isCancelled);
		}

		@Override
		public boolean isDone() {
			return this.tasks
				.stream()
				.allMatch(Future::isDone);
		}

		@Override
		public Boolean get() throws InterruptedException, ExecutionException {
			ExceptionHandler exceptionHandler = new ExceptionHandler();
			for (Future<?> task : this.tasks) {
				try {
					task.get();
				} catch (ExecutionException e) {
					processException(exceptionHandler, e);
				}
			}

			if (!exceptionHandler.isEmpty())
				throw new ExecutionException("An exception occurred while running listener",
					exceptionHandler.toException(EventDispatchException::new));

			return !CancellableEvent.isCanceled(this.event);
		}

		@Override
		public Boolean get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			Objects.requireNonNull(unit, "unit is null");

			long deadline = System.nanoTime() + unit.toNanos(timeout);

			ExceptionHandler exceptionHandler = new ExceptionHandler();
			for (Future<?> task : this.tasks) {
				long remaining = deadline - System.nanoTime();
				if (remaining <= 0)
					throw new TimeoutException();

				try {
					task.get(remaining, TimeUnit.NANOSECONDS);
				} catch (ExecutionException e) {
					processException(exceptionHandler, e);
				}
			}

			if (!exceptionHandler.isEmpty()) {
				throw new ExecutionException("An exception occurred while running listener",
					exceptionHandler.toException(EventDispatchException::new));
			}

			return !CancellableEvent.isCanceled(this.event);
		}

		private static void processException(@NotNull ExceptionHandler handler, @NotNull ExecutionException exception) {
			Objects.requireNonNull(handler, "handler is null");
			Objects.requireNonNull(exception, "exception is null");

			Throwable cause = exception.getCause();
			if (cause instanceof ListenerException ex)
				handler.accept(ex.getCause());
			else
				handler.accept(cause);
		}
	}
}
