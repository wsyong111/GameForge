package io.github.wsyong11.gameforge.framework.event;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class CancellableEvent extends Event {
	public static boolean isCanceled(@NotNull Event event) {
		Objects.requireNonNull(event, "event is null");
		return event instanceof CancellableEvent e && e.isCanceled();
	}

	private boolean canceled;

	public CancellableEvent() {
		this.canceled = false;
	}

	public boolean isCanceled() {
		return this.canceled;
	}

	protected void setCanceled(boolean cancel) {
		this.canceled = cancel;
	}

	public void cancel() {
		this.setCanceled(true);
	}
}
