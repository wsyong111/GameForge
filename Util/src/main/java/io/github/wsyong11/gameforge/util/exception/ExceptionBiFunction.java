package io.github.wsyong11.gameforge.util.exception;

@FunctionalInterface
public interface ExceptionBiFunction<T, U, R, E extends Throwable> {
	R apply(T o, U o1) throws E;
}
