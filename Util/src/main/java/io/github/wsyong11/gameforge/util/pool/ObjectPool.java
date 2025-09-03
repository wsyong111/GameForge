package io.github.wsyong11.gameforge.util.pool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ObjectPool<T> {
	@NotNull
	static <V> ObjectPool<V> create(int maxSize, @NotNull Supplier<V> objectConstructor, @NotNull Consumer<V> resetMethod) {
		Objects.requireNonNull(objectConstructor, "objectConstructor is null");
		Objects.requireNonNull(resetMethod, "resetMethod is null");

		return new SimpleObjectPool<>(maxSize, objectConstructor, resetMethod);
	}

	@NotNull
	T acquire();

	void release(@Nullable T obj);

	int size();

	int getMaxSize();

	@NotNull
	default ObjectPool<T> prewarm(int count) {
		int toCreate = Math.min(count, this.getMaxSize() - this.size());

		Object[] temp = new Object[toCreate];
		for (int i = 0; i < toCreate; i++)
			temp[i] = this.acquire();

		for (Object obj : temp) {
			//noinspection unchecked
			this.release((T) obj);
		}

		return this;
	}
}
