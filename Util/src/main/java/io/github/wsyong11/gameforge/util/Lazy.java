package io.github.wsyong11.gameforge.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 懒加载值类，用于延迟初始化值
 *
 * @param <T> 懒加载值的类型
 */
public abstract class Lazy<T> implements Supplier<T> {
	/**
	 * 构建一个懒加载对象
	 *
	 * @param supplier 值的提供器，将在第一次获取值时调用
	 */
	@NotNull
	public static <T> Lazy<T> of(@NotNull Supplier<T> supplier) {
		Objects.requireNonNull(supplier, "supplier is null");
		return new Fast<>(supplier);
	}

	/**
	 * 构建线程安全懒加载对象
	 *
	 * @param supplier 值的提供器，将在第一次获取值时调用
	 */
	@NotNull
	public static <T> Lazy<T> concurrentOf(@NotNull Supplier<T> supplier) {
		Objects.requireNonNull(supplier, "supplier is null");
		return new Concurrent<>(supplier);
	}

	private static class Fast<T> extends Lazy<T> {
		private Supplier<T> supplier;
		private T instance;

		private Fast(@NotNull Supplier<T> supplier) {
			Objects.requireNonNull(supplier, "supplier is null");
			this.supplier = supplier;
			this.instance = null;
		}

		@Override
		public T get() {
			if (this.supplier == null)
				return this.instance;

			this.instance = this.supplier.get();
			this.supplier = null;
			return this.instance;
		}
	}

	private static class Concurrent<T> extends Lazy<T> {
		private volatile Supplier<T> supplier;

		private volatile Object lock;
		private volatile T instance;

		private Concurrent(@NotNull Supplier<T> supplier) {
			Objects.requireNonNull(supplier, "supplier is null");
			this.supplier = supplier;

			this.instance = null;
			this.lock = new Object();
		}

		@Override
		public T get() {
			if (this.supplier == null)
				return this.instance;

			Object localLock = this.lock;
			synchronized (localLock) {
				if (this.supplier != null) {
					this.instance = this.supplier.get();
					this.supplier = null;
					this.lock = null;
				}
			}

			return this.instance;
		}
	}
}
