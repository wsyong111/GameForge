package io.github.wsyong11.gameforge.util.exception;

@FunctionalInterface
public interface ExceptionFunction<T, R, E extends Throwable> {
	R apply(T o) throws E;
}
