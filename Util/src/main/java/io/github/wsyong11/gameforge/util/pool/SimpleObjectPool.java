package io.github.wsyong11.gameforge.util.pool;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleObjectPool<T> extends AbstractObjectPool<T> {
	private final Supplier<T> objectConstructor;
	private final Consumer<T> resetMethod;

	public SimpleObjectPool(int maxSize, @NotNull Supplier<T> objectConstructor, @NotNull Consumer<T> resetMethod) {
		super(maxSize);
		Objects.requireNonNull(objectConstructor, "objectConstructor is null");
		Objects.requireNonNull(resetMethod, "resetMethod is null");

		this.objectConstructor = objectConstructor;
		this.resetMethod = resetMethod;
	}

	@NotNull
	@Override
	protected T createObject() {
		return this.objectConstructor.get();
	}

	@Override
	protected void resetObject(@NotNull T obj) {
		this.resetMethod.accept(obj);
	}
}
