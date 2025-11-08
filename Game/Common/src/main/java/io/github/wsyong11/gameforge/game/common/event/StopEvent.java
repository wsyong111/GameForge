package io.github.wsyong11.gameforge.game.common.event;

import io.github.wsyong11.gameforge.framework.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StopEvent extends Event {
	private final Type type;

	public StopEvent(@NotNull Type type) {
		Objects.requireNonNull(type, "type is null");
		this.type=type;
	}

	@NotNull
	public Type getType() {
		return this.type;
	}

	public enum Type {
		NORMAL,
		FATAL_ERROR,
		FORCE_QUIT
	}
}
