package io.github.wsyong11.gameforge.framework.debug;

import org.jetbrains.annotations.NotNull;

public interface DebugInstanceProvider<T> {
	/**
	 * 获取当前调试提供器支持的类型
	 */
	@NotNull
	Class<T> getSupportClass();

	/**
	 * 判断当前实例是否可以被调试包装
	 */
	default boolean canProcess(@NotNull T instance) {
		return true;
	}

	/**
	 * 返回调试包装后的实例
	 */
	@NotNull
	<V extends T> T wrap(@NotNull V instance);

	default void dump(@NotNull StringBuilder sb, @NotNull T instance){
		throw new UnsupportedOperationException();
	}
}

