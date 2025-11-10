package io.github.wsyong11.gameforge.util.exception;

@FunctionalInterface
public interface ExceptionSupplier<T, E extends Throwable> {
	T get() throws E;
}
