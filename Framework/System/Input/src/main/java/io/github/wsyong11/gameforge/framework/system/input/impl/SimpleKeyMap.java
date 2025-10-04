package io.github.wsyong11.gameforge.framework.system.input.impl;

import io.github.wsyong11.gameforge.framework.key.InputKey;
import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.system.input.KeyMap;
import io.github.wsyong11.gameforge.framework.tick.TickSourceProvider;
import io.github.wsyong11.gameforge.framework.tick.Tickable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SimpleKeyMap implements KeyMap {
	private final TickSourceProvider tickSource;

	private final Map<InputKey, KeyAction> keyMap;
	private final Map<InputKey, Long> pressedTickMap;

	public SimpleKeyMap(@NotNull TickSourceProvider tickSource) {
		Objects.requireNonNull(tickSource, "tickSource is null");
		this.tickSource=tickSource;
	}

	@Override
	public boolean isPressed(@NotNull InputKey key) {
		return false;
	}

	@Override
	public @NotNull KeyAction getAction(@NotNull InputKey key) {
		return null;
	}

	@Override
	public long getPressedTick(@NotNull InputKey key) {
		return 0;
	}

	@Override
	public long getPressTimeMs(@NotNull InputKey key) {
		return 0;
	}

	@Override
	public @NotNull @UnmodifiableView Set<InputKey> getPressedKeys() {
		return Set.of();
	}

	@Override
	public @NotNull @UnmodifiableView Map<InputKey, KeyAction> getKeys() {
		return Map.of();
	}
}
