package io.github.wsyong11.gameforge.framework.system.input.impl;

import io.github.wsyong11.gameforge.framework.key.InputKey;
import io.github.wsyong11.gameforge.framework.system.input.InputManager;
import io.github.wsyong11.gameforge.framework.system.input.KeyMap;
import io.github.wsyong11.gameforge.framework.system.input.listener.KeyListener;
import io.github.wsyong11.gameforge.framework.system.input.listener.MouseListener;
import io.github.wsyong11.gameforge.framework.tick.Tickable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2dc;

public class SimpleInputManager implements Tickable, InputManager {
	public void processInput(@NotNull InputKey key, boolean pressed) {

	}

	@Override
	public @NotNull KeyMap getKeyMap() {
		return null;
	}

	@Override
	public @NotNull Vector2dc getMousePosition() {
		return null;
	}

	@Override
	public void registerMouseListener(@NotNull MouseListener listener) {

	}

	@Override
	public void unregisterMouseListener(@NotNull MouseListener listener) {

	}

	@Override
	public void registerKeyListener(@NotNull KeyListener listener) {

	}

	@Override
	public void unregisterKeyListener(@NotNull KeyListener listener) {

	}

	@Override
	public void tick(long currentTick) {

	}
}
