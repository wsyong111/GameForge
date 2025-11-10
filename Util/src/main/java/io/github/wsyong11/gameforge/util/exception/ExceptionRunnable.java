package io.github.wsyong11.gameforge.util.exception;

@FunctionalInterface
public interface ExceptionRunnable<T extends Throwable> {
	void run() throws T;
}
