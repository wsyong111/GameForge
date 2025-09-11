package io.github.wsyong11.gameforge.framework.system.window.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.system.window.input.InputEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;

public interface WindowInputListener extends IListener {
	void onInput(@NotNull InputEvent event);
}
