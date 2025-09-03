package io.github.wsyong11.gameforge.game.common;

import io.github.wsyong11.gameforge.framework.lifecycle.LifecycleProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 游戏生命周期入口
 */
public interface Game extends LifecycleProvider {
	@NotNull
	GameEnvConfig getEnvConfig();

	@Nullable
	GameContext getContext();
}
