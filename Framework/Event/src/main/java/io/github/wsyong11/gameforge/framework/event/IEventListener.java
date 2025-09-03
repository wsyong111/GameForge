package io.github.wsyong11.gameforge.framework.event;

import org.jetbrains.annotations.NotNull;

/**
 * 事件监听器接口
 *
 * @param <T> 事件类型
 * @apiNote 如果要使用 {@link IEventBus#register(IEventListener) 类型推断} 注册，请勿写成 lambda 形式，这会导致泛型数据丢失
 * @implNote {@link IEventBus#register(IEventListener) 类型推断} 将获取 {@link T} 的实际类型
 */
@FunctionalInterface
public interface IEventListener<T extends Event> {
	/**
	 * 事件监听函数
	 *
	 * @param event 事件实例
	 */
	void onEvent(@NotNull T event);
}
