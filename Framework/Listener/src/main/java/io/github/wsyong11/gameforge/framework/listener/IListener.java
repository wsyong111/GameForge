package io.github.wsyong11.gameforge.framework.listener;

import io.github.wsyong11.gameforge.util.Nameable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

/**
 * 监听器基类
 */
public interface IListener extends Nameable {
	/**
	 * 获取监听器的名称
	 *
	 * @return 监听器名称
	 */
	@NotNull
	@Override
	default String getName() {
		return this.getClass().getName() + "[0x" + Integer.toHexString(Objects.hashCode(this)).toUpperCase(Locale.ROOT) + "]";
	}
}
