package io.github.wsyong11.gameforge.framework.context;

import io.github.wsyong11.gameforge.framework.annotation.CallerSensitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Objects;

public class DebugInfo {
	@CallerSensitive
	@NotNull
	public static DebugInfo of(@NotNull Thread thread) {
		Objects.requireNonNull(thread, "thread is null");

		long id = thread.getId();
		Thread.State state = thread.getState();
		int priority = thread.getPriority();
		String name = thread.getName();
		boolean daemon = thread.isDaemon();
		List<StackTraceElement> stackTrace = List.of(thread.getStackTrace());

		return new DebugInfo(name, id, state, daemon, priority, stackTrace);
	}

	private final String name;
	private final long id;
	private final Thread.State state;
	private final boolean daemon;
	private final int priority;
	private final List<StackTraceElement> stackTrace;

	public DebugInfo(
		@NotNull String name,
		long id,
		@NotNull Thread.State state,
		boolean daemon,
		int priority,
		@NotNull List<StackTraceElement> stackTrace
	) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(state, "state is null");
		Objects.requireNonNull(stackTrace, "stackTrace is null");

		this.name = name;
		this.id = id;
		this.state = state;
		this.daemon = daemon;
		this.priority = priority;
		this.stackTrace = List.copyOf(stackTrace);
	}

	@NotNull
	public String getName() {
		return this.name;
	}

	public long getId() {
		return this.id;
	}

	@NotNull
	public Thread.State getState() {
		return this.state;
	}

	public boolean isDaemon() {
		return this.daemon;
	}

	public int getPriority() {
		return this.priority;
	}

	@NotNull
	@UnmodifiableView
	public List<StackTraceElement> getStackTrace() {
		return this.stackTrace;
	}
}
