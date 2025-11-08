package io.github.wsyong11.gameforge.util.exception;

public interface ExceptionIntSupplier<T extends Throwable> {
	int getAsInt() throws T;
}
