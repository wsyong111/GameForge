package io.github.wsyong11.gameforge.game.common;

import io.github.wsyong11.gameforge.framework.lifecycle.LifecycleProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 游戏生命周期入口
 */
public interface Game extends LifecycleProvider {
	int DEFAULT_TPS = 20;

	/**
	 * 获取游戏环境配置
	 *
	 * @return 可用于访问游戏环境配置的对象
	 */
	@NotNull
	GameEnvConfig getEnvConfig();

	/**
	 * 获取当前的游戏上下文对象
	 *
	 * @return 当前游戏的上下文对象
	 * @see GameContext
	 */
	@NotNull
	GameContext getContext();
}
