package io.github.wsyong11.gameforge.util.exception;

@FunctionalInterface
public interface ExceptionRunnable<E extends Throwable> {
	void run() throws E;
}
