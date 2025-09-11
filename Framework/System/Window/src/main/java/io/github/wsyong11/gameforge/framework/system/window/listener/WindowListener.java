package io.github.wsyong11.gameforge.framework.system.window.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.system.window.WindowDisplayType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;

public interface WindowListener extends IListener {
	void onResize(@NotNull Vector2ic newSize);

	void onMove(@NotNull Vector2ic newPosition);

	void onFocusChanged(boolean isFocus);

	void onDisplayTypeChanged(@NotNull WindowDisplayType newType);

	void onClose();
}
