package io.github.wsyong11.gameforge.util.exception;

import org.jetbrains.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExceptionHandler implements Consumer<Throwable> {
	@NotNull
	public static ExceptionHandler create() {
		return new ExceptionHandler();
	}

	private final List<Throwable> exceptions;

	public ExceptionHandler() {
		this.exceptions = new ArrayList<>();
	}

	public boolean isEmpty() {
		return this.exceptions.isEmpty();
	}

	public int size() {
		return this.exceptions.size();
	}

	@UnmodifiableView
	public List<Throwable> toList() {
		return Collections.unmodifiableList(this.exceptions);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public ExceptionHandler run(@Nullable Runnable runnable) {
		if (runnable == null)
			return this;

		try {
			runnable.run();
		} catch (Throwable e) {
			this.exceptions.add(e);
		}

		return this;
	}

	@Contract("null -> null")
	@UnknownNullability
	public <T> T run(@Nullable Callable<T> callable) {
		if (callable == null)
			return null;

		try {
			return callable.call();
		} catch (Throwable e) {
			this.exceptions.add(e);
			return null;
		}
	}

	@Contract("null -> null")
	@UnknownNullability
	public <T> T run(@Nullable Supplier<T> supplier) {
		if (supplier == null)
			return null;

		try {
			return supplier.get();
		} catch (Throwable e) {
			this.exceptions.add(e);
			return null;
		}
	}

	@Contract("_, null -> null")
	@UnknownNullability
	public <T, R> R run(@UnknownNullability T p0, @Nullable Function<T, R> function) {
		if (function == null)
			return null;

		try {
			return function.apply(p0);
		} catch (Throwable e) {
			this.exceptions.add(e);
			return null;
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public void throwException() {
		this.throwException(RuntimeException::new);
	}

	public void throwException(@NotNull String message) {
		this.throwException(message, RuntimeException::new);
	}

	public <T extends Throwable> void throwException(@NotNull String message, @NotNull Function<String, T> exceptionBuilder) throws T {
		Objects.requireNonNull(exceptionBuilder, "exceptionBuilder is null");

		if (this.exceptions.isEmpty())
			return;

		throw this.toException(message, exceptionBuilder);
	}

	public <T extends Throwable> void throwException(@NotNull Supplier<T> exceptionBuilder) throws T {
		Objects.requireNonNull(exceptionBuilder, "exceptionBuilder is null");

		if (this.exceptions.isEmpty())
			return;

		throw this.toException(exceptionBuilder);
	}

	@NotNull
	public <T extends Throwable> T toException(@NotNull String message, @NotNull Function<String, T> exceptionBuilder) {
		Objects.requireNonNull(exceptionBuilder, "exceptionBuilder is null");
		return this.fillException(exceptionBuilder.apply(message));
	}

	@NotNull
	public <T extends Throwable> T toException(@NotNull Supplier<T> exceptionBuilder) {
		Objects.requireNonNull(exceptionBuilder, "exceptionBuilder is null");
		return this.fillException(exceptionBuilder.get());
	}

	@Contract("null -> null; !null -> !null")
	@Nullable
	public <T extends Throwable> T fillException(@Nullable T exception) {
		if (exception == null)
			return null;

		int size = this.exceptions.size();
		if (size == 0)
			return exception;

		exception.initCause(this.exceptions.get(0));
		if (size == 1)
			return exception;

		this.exceptions.forEach(exception::addSuppressed);
		return exception;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void accept(@Nullable Throwable throwable) {
		if (throwable == null)
			return;
		this.exceptions.add(throwable);
	}

	@Override
	public String toString() {
		return this.exceptions.toString();
	}
}
