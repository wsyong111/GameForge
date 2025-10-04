package io.github.wsyong11.gameforge.framework.system.input.listener;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.MouseButton;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2dc;

public interface MouseListener extends IListener {
	void onMouseMove(@NotNull Vector2dc position);

	void onMouseFocusChange(boolean entered);
}
