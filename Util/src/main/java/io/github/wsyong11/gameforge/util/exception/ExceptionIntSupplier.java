package io.github.wsyong11.gameforge.util.exception;

@FunctionalInterface
public interface ExceptionIntSupplier<T extends Throwable> {
	int getAsInt() throws T;
}
