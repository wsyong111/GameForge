package io.github.wsyong11.gameforge.util.exception;

public interface ExceptionRunnable<T extends Throwable> {
	void run() throws T;
}
