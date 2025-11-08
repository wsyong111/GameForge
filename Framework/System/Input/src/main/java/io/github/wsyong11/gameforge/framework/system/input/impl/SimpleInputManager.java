package io.github.wsyong11.gameforge.framework.system.input.impl;

import com.google.common.collect.Maps;
import io.github.wsyong11.gameforge.framework.key.InputKey;
import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.system.input.InputManager;
import io.github.wsyong11.gameforge.framework.system.input.KeyMap;
import io.github.wsyong11.gameforge.framework.system.input.listener.KeyListener;
import io.github.wsyong11.gameforge.framework.system.input.listener.MouseListener;
import io.github.wsyong11.gameforge.framework.tick.TickInfo;
import io.github.wsyong11.gameforge.framework.tick.TickSourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector2dc;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 2025/11/8 Impl input manager
public class SimpleInputManager implements InputManager {
	private final TickSourceProvider tickSource;

	private final Map<InputKey, KeyAction> keyMap;
	private final Map<InputKey, Long> pressTickMap;

	public SimpleInputManager(@NotNull TickSourceProvider tickSource) {
		Objects.requireNonNull(tickSource, "tickSource is null");
		this.tickSource = tickSource;

		this.keyMap = new ConcurrentHashMap<>();
		this.pressTickMap = new ConcurrentHashMap<>();
	}

	@Override
	public void processInput(@NotNull InputKey key, boolean pressed) {

	}

	@NotNull
	@Override
	public KeyMap getKeyMap() {
		return new SimpleKeyMap();
	}

	@NotNull
	@Override
	public Vector2dc getMousePosition() {
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
	public void tick(@NotNull TickInfo info) {

	}

	private class SimpleKeyMap implements KeyMap {
		@Override
		public boolean isPressed(@NotNull InputKey key) {
			Objects.requireNonNull(key, "key is null");
			return this.getAction(key).isPressed();
		}

		@NotNull
		@Override
		public KeyAction getAction(@NotNull InputKey key) {
			Objects.requireNonNull(key, "key is null");
			return keyMap.getOrDefault(key, KeyAction.UP);
		}

		@Override
		public long getPressedTick(@NotNull InputKey key) {
			Objects.requireNonNull(key, "key is null");

			Long pressTick = pressTickMap.get(key);
			if (pressTick == null)
				return 0L;

			return tickSource.getCurrentTick() - pressTick;
		}

		@Override
		@NotNull
		@UnmodifiableView
		public Set<InputKey> getPressedKeys() {
			return Collections.unmodifiableSet(Maps.filterValues(keyMap, KeyAction::isPressed).keySet());
		}

		@Override
		@NotNull
		@UnmodifiableView
		public Map<InputKey, KeyAction> getKeys() {
			return Collections.unmodifiableMap(keyMap);
		}
	}
}
