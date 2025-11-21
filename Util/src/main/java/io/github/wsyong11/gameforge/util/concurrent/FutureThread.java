package io.github.wsyong11.gameforge.util.concurrent;

import io.github.wsyong11.gameforge.util.exception.ExceptionSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class FutureThread<T> extends Thread {
	@Nullable
	private final ExceptionSupplier<T, Throwable> supplier;

	private final CompletableFuture<T> future;

	public FutureThread() {
		this(null);
	}

	public FutureThread(@Nullable ExceptionSupplier<T, Throwable> supplier) {
		Objects.requireNonNull(supplier, "supplier is null");

		this.supplier = supplier;

		this.future = new CompletableFuture<>();
	}

	@NotNull
	public CompletableFuture<T> getFuture() {
		return this.future.thenApply(Function.identity());
	}

	protected T runFuture() throws Throwable {
		return this.supplier != null ? this.supplier.get() : null;
	}

	@Override
	public void run() {
		try {
			this.future.complete(this.runFuture());
		} catch (Throwable e) {
			this.future.completeExceptionally(e);
		}
	}
}
