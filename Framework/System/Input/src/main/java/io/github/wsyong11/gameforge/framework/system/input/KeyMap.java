package io.github.wsyong11.gameforge.framework.system.input;

import io.github.wsyong11.gameforge.framework.key.InputKey;
import io.github.wsyong11.gameforge.framework.key.KeyAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.Set;

public interface KeyMap {
	boolean isPressed(@NotNull InputKey key);

	@NotNull
	KeyAction getAction(@NotNull InputKey key);

	// 没有按下时就是0
	long getPressedTick(@NotNull InputKey key);

	// 没有按下时就是0
	long getPressTimeMs(@NotNull InputKey key);

	/**
	 * 获取所有已激活的按键集合，该集合包含所有按键除了 {@link KeyAction#UP} 状态的按键
	 * 返回的集合将会随着用户输入更新
	 *
	 * @return 所有已激活的按键的集合，从未按下的按键可能不包含在内
	 */
	@NotNull
	@UnmodifiableView
	Set<InputKey> getPressedKeys();

	/**
	 * 获取所有按键的状态表，
	 * 返回的表将会随着用户输入更新
	 *
	 * @return 所有按键的状态列表，从未按下的按键可能不包含在内，建议通过 {@link Map#getOrDefault(Object, Object) getOrDefault} 获取状态
	 */
	@NotNull
	@UnmodifiableView
	Map<InputKey, KeyAction> getKeys();
}
