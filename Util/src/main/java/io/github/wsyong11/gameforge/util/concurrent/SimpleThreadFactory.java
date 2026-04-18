package io.github.wsyong11.gameforge.util.concurrent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleThreadFactory implements ThreadFactory {
	@NotNull
	public static Builder builder() {
		return new Builder();
	}

	private final boolean daemon;
	private final int priority;
	private final String name;
	private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	private final AtomicInteger id;

	public SimpleThreadFactory(@NotNull String name) {
		this(false, name);
	}

	public SimpleThreadFactory(boolean daemon, @NotNull String name) {
		this(daemon, Thread.NORM_PRIORITY, name);
	}

	public SimpleThreadFactory(boolean daemon, int priority, @NotNull String name) {
		this(daemon, priority, name, null);
	}

	public SimpleThreadFactory(
		boolean daemon,
		int priority,
		@NotNull String name,
		@Nullable Thread.UncaughtExceptionHandler uncaughtExceptionHandler
	) {
		Objects.requireNonNull(name, "name is null");

		this.daemon = daemon;
		this.priority = priority;
		this.name = name;
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;

		this.id = new AtomicInteger(0);
	}

	@Override
	public Thread newThread(@NotNull Runnable r) {
		Objects.requireNonNull(r, "r is null");

		int id = this.id.getAndIncrement();

		Thread thread = new Thread(r);
		thread.setDaemon(this.daemon);
		thread.setPriority(this.priority);
		thread.setName(this.name + " - " + id);
		thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);

		return thread;
	}

	public static class Builder {
		private boolean daemon = false;
		private int priority = Thread.NORM_PRIORITY;
		private String name = "Pool";
		private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;

		@NotNull
		public Builder daemon() {
			return this.daemon(true);
		}

		@NotNull
		public Builder daemon(boolean enable) {
			this.daemon = enable;
			return this;
		}

		@NotNull
		public Builder priority(int priority) {
			this.priority = priority;
			return this;
		}

		@NotNull
		public Builder name(@NotNull String name) {
			Objects.requireNonNull(name, "name is null");
			this.name = name;
			return this;
		}

		@NotNull
		public Builder uncaughtExceptionHandler(@Nullable Thread.UncaughtExceptionHandler handler) {
			this.uncaughtExceptionHandler = handler;
			return this;
		}

		@NotNull
		public SimpleThreadFactory build() {
			return new SimpleThreadFactory(this.daemon, this.priority, this.name, this.uncaughtExceptionHandler);
		}
	}
}
