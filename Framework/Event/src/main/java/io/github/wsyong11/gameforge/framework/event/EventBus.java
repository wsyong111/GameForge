package io.github.wsyong11.gameforge.framework.event;

import io.github.wsyong11.gameforge.framework.event.bus.SimpleEventBus;
import io.github.wsyong11.gameforge.framework.event.ex.EventDispatchException;
import io.github.wsyong11.gameforge.framework.event.ex.EventListenerExceptionCallback;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 事件总线完整功能接口。
 * <p>
 * 定义了事件的分发、异常处理，以及同步/异步的调用方式。
 */
public interface EventBus extends IEventBus {
	@NotNull
	static EventBus simple() {
		return new SimpleEventBus();
	}

	/**
	 * 注销所有注册的监听器。
	 */
	void unregisterAll();

	/**
	 * 同步发布一个事件。
	 * 所有监听器将在当前线程中按照优先级依次执行。
	 *
	 * @param event 要发布的事件实例
	 * @param <T>   事件类型
	 * @return 如果事件为 {@link CancellableEvent} 且未被取消，则返回 true，否则返回 false
	 * @throws EventDispatchException 当任意监听器抛出异常时，会将所有异常收集并抛出，
	 *                                可通过 {@link EventDispatchException#getCause()} 获取第一个异常
	 */
	<T extends Event> boolean post(@NotNull T event) throws EventDispatchException;

	/**
	 * 同步发布一个事件。
	 * 所有监听器将在当前线程中按照优先级依次执行。
	 *
	 * @param event             要发布的事件实例
	 * @param <T>               事件类型
	 * @param exceptionCallback 监听器执行过程中发生的异常将传递给此回调
	 * @return 如果事件为 {@link CancellableEvent} 且未被取消，则返回 true，否则返回 false
	 */
	<T extends Event> boolean post(@NotNull T event, @NotNull EventListenerExceptionCallback exceptionCallback);

	/**
	 * 异步发布一个事件。
	 * 所有监听器将在指定的线程池中按照优先级依次执行。
	 *
	 * @param executor 线程池
	 * @param event    要发布的事件实例
	 * @param <T>      事件类型
	 * @return 一个 {@link Future} 对象，调用 {@link Future#get()} 可获得事件是否被取消的结果，
	 * 若监听器执行异常，则会抛出 {@link java.util.concurrent.ExecutionException}，
	 * 其原因始终为 {@link EventDispatchException}
	 */
	@NotNull
	<T extends Event> Future<Boolean> postAsync(@NotNull ExecutorService executor, @NotNull T event);

	/**
	 * 异步发布一个事件。
	 * 所有监听器将在指定的线程池中按照优先级依次执行。
	 *
	 * @param executor          线程池
	 * @param event             要发布的事件实例
	 * @param <T>               事件类型
	 * @param exceptionCallback 监听器执行过程中发生的异常会被收集，并在所有监听器执行完毕后统一传递给此回调，回调可能会在线程池线程中运行
	 * @return 一个 {@link Future} 对象，调用 {@link Future#get()} 可获得事件是否被取消的结果
	 */
	@NotNull
	<T extends Event> Future<Boolean> postAsync(
		@NotNull ExecutorService executor,
		@NotNull T event,
		@NotNull EventListenerExceptionCallback exceptionCallback
	);
}
