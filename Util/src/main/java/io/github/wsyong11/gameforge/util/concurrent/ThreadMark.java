package io.github.wsyong11.gameforge.util.concurrent;

import org.jetbrains.annotations.NotNull;

public class ThreadMark {
	private static final ThreadLocal<ThreadMark> MARK = ThreadLocal.withInitial(ThreadMark::new);

	@NotNull
	public static ThreadMark get() {
		return MARK.get();
	}

	private final Thread thread;

	private ThreadMark() {
		this.thread = Thread.currentThread();
	}

	@NotNull
	public Thread getThread() {
		return this.thread;
	}

	public void checkAssert() {
		Thread current = Thread.currentThread();
		if (current != this.thread)
			throw new IllegalStateException("Check fail, Current: " + current + ", Mark: " + this.thread);
	}

	public boolean check() {
		return Thread.currentThread() == this.thread;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ThreadMark that = (ThreadMark) o;
		return this.thread == that.thread;
	}

	@Override
	public int hashCode() {
		return this.thread.hashCode();
	}

	@Override
	public String toString() {
		return "Mark[\"%s\" 0x%08X]".formatted(
			this.thread.getName(),
			this.thread.getId());
	}
}
