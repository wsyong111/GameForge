package io.github.wsyong11.gameforge.framework.system.render.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;

public interface LogicSizeListener extends IListener {
	void onLogicSizeChanged(@NotNull Vector2ic newSize);
}
