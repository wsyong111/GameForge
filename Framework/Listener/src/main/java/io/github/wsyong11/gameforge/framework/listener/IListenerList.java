package io.github.wsyong11.gameforge.framework.listener;

import org.jetbrains.annotations.NotNull;

/**
 * 基本监听器列表
 */
public interface IListenerList {
	/**
	 * 添加监听器
	 *
	 * @param type     监听器类型
	 * @param listener 监听器对象
	 * @param <T>      监听器类型
	 */
	<T extends IListener> void add(@NotNull Class<T> type, @NotNull T listener);

	/**
	 * 移除监听器
	 *
	 * @param type     监听器类型
	 * @param listener 监听器对象
	 * @param <T>      监听器类型
	 */
	<T extends IListener> void remove(@NotNull Class<T> type, @NotNull T listener);
}
