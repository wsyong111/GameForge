package io.github.wsyong11.gameforge.framework.listener;

import io.github.wsyong11.gameforge.framework.listener.ex.ListenerException;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.listener.list.AsyncListenerList;
import io.github.wsyong11.gameforge.framework.listener.list.NamedListenerList;
import io.github.wsyong11.gameforge.framework.listener.list.SyncListenerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * 监听器列表
 */
public interface ListenerList extends IListenerList {
	@NotNull
	static ListenerList sync(){
		return new SyncListenerList();
	}

	@NotNull
	static ListenerList async(@NotNull Executor executor) {
		Objects.requireNonNull(executor, "executor is null");
		return new AsyncListenerList(executor);
	}

	@NotNull
	static ListenerList named(@NotNull ListenerList delegate, @NotNull String name) {
		Objects.requireNonNull(delegate, "delegate is null");
		Objects.requireNonNull(name, "name is null");
		return new NamedListenerList(delegate, name);
	}

	/**
	 * 调用对应类型的监听器
	 *
	 * @param type              监听器类型
	 * @param trigger           监听器触发器
	 * @param exceptionCallback 监听器错误回调函数
	 * @param <T>               监听器类型
	 */
	<T extends IListener> void fire(@NotNull Class<T> type, @NotNull Consumer<T> trigger, @NotNull ListenerExceptionCallback<T> exceptionCallback);

	/**
	 * 调用对应类型的监听器，并在出错时打印错误信息
	 *
	 * @param type    监听器类型
	 * @param trigger 监听器触发器
	 * @param <T>     监听器类型
	 * @throws ListenerException 触发监听器时触发的异常，{@link ListenerException#getCause()} 将被设定为第一个异常，所有异常可以通过 {@link ListenerException#getSuppressed()} 获取
	 */
	<T extends IListener> void fire(@NotNull Class<T> type, @NotNull Consumer<T> trigger) throws ListenerException;

	/**
	 * 获取监听器类型对应的监听器数量
	 *
	 * @param type 监听器类型
	 * @return 监听器数量
	 */
	int size(@NotNull Class<? extends IListener> type);

	/**
	 * 获取所有类型的监听器数量的总和
	 *
	 * @return 监听器数量
	 */
	int size();

	/**
	 * 清空特定类型的监听器
	 *
	 * @param type 监听器类型
	 */
	void clear(@NotNull Class<? extends IListener> type);

	/**
	 * 清空所有类型的监听器
	 */
	void clear();
}
