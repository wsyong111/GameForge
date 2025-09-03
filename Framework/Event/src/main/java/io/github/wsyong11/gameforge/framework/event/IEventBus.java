package io.github.wsyong11.gameforge.framework.event;

import io.github.wsyong11.gameforge.framework.event.reflect.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 事件总线接口，可用于注册事件监听器，允许多次注册监听器
 */
public interface IEventBus {
	/**
	 * 注册一个事件监听器。
	 *
	 * @param type     监听的事件类型
	 * @param priority 优先级（高优先级先执行）
	 * @param listener 监听器实例
	 * @param <T>      事件类型
	 */
	<T extends Event> void register(@NotNull Class<T> type, @NotNull EventPriority priority, @NotNull IEventListener<? super T> listener);

	/**
	 * 注册一个事件监听器（根据泛型类型推断事件类型）。
	 * 若无法推断类型，则抛出异常。
	 *
	 * @param priority 事件优先级
	 * @param listener 事件监听器
	 * @param <T>      事件类型
	 */
	<T extends Event> void register(@NotNull EventPriority priority, @NotNull IEventListener<T> listener);

	/**
	 * 注册一个事件监听器，使用默认优先级 {@link EventPriority#NORMAL}。
	 *
	 * @param listener 事件监听器
	 * @param <T>      事件类型
	 */
	default <T extends Event> void register(@NotNull IEventListener<T> listener) {
		this.register(EventPriority.NORMAL, listener);
	}

	/**
	 * 注册一个事件监听器，使用默认优先级 {@link EventPriority#NORMAL}。
	 *
	 * @param type     监听的事件类型
	 * @param listener 事件监听器
	 * @param <T>      事件类型
	 */
	default <T extends Event> void register(@NotNull Class<T> type, @NotNull IEventListener<T> listener) {
		this.register(type, EventPriority.NORMAL, listener);
	}

	/**
	 * 注册一个对象中的所有事件处理方法（带有 {@link SubscribeEvent} 注解的方法）。
	 *
	 * @param obj 要注册的对象实例
	 */
	void register(@NotNull Object obj);

	/**
	 * 注册一个类中的所有静态事件处理方法（带有 {@link SubscribeEvent} 注解的方法）。
	 *
	 * @param obj 要注册的类（Class）
	 */
	void register(@NotNull Class<?> obj);


	/**
	 * 注销一个已注册的事件监听器。
	 *
	 * @param listener 要注销的监听器
	 * @param <T>      事件类型
	 */
	<T extends Event> void unregister(@NotNull IEventListener<T> listener);

	/**
	 * 注销一个对象及其所有注册的事件监听器。
	 *
	 * @param obj 对象实例
	 */
	void unregister(@NotNull Object obj);

	/**
	 * 注销一个类及其所有注册的静态事件监听器。
	 *
	 * @param obj 类（Class）
	 */
	void unregister(@NotNull Class<?> obj);
}
