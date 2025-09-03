package io.github.wsyong11.gameforge.framework.listener.ex;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 监听器错误回调
 *
 * @param <T> 监听器类型
 */
@FunctionalInterface
public interface ListenerExceptionCallback<T extends IListener> {
	/**
	 * 忽略监听器错误
	 *
	 * @param <V> 监听器类型
	 * @return 监听器错误回调对象
	 */
	@NotNull
	static <V extends IListener> ListenerExceptionCallback<V> ignore() {
		return (listener, ex) -> { /* no-op */ };
	}

	@NotNull
	static <V extends IListener> ListenerExceptionCallback<V> log(@NotNull Logger logger) {
		Objects.requireNonNull(logger, "logger is null");
		return (listener, error) ->
			logger.error("An exception occurred when triggering the listener {}", listener, error);
	}

	/**
	 * 监听器错误回调函数
	 *
	 * @param listener 出错的监听器
	 * @param error    错误内容
	 */
	void onListenerError(@NotNull T listener, @NotNull Throwable error);
}
