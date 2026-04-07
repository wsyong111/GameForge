package io.github.wsyong11.gameforge.util.collection;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.PrimitiveIterator;

@UtilityClass
public class ArrayIterators {
	@NotNull
	public static <T> Iterator<T> iterator(T @NotNull [] array) {
		Objects.requireNonNull(array, "array is null");
		return new ArrayIterator<>(array);
	}

	@NotNull
	public static PrimitiveIterator.OfInt iterator(int @NotNull [] array) {
		Objects.requireNonNull(array, "array is null");
		return new IntArrayIterator(array);
	}

	@NotNull
	public static PrimitiveIterator.OfLong iterator(long @NotNull [] array) {
		Objects.requireNonNull(array, "array is null");
		return new LongArrayIterator(array);
	}

	@NotNull
	public static PrimitiveIterator.OfDouble iterator(double @NotNull [] array) {
		Objects.requireNonNull(array, "array is null");
		return new DoubleArrayIterator(array);
	}

	private static class ArrayIterator<T> implements Iterator<T> {
		private final T[] array;
		private int index;

		private ArrayIterator(T @NotNull [] array) {
			Objects.requireNonNull(array, "array is null");
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			return this.index < this.array.length;
		}

		@Override
		public T next() {
			return this.array[this.index++];
		}
	}

	private static class IntArrayIterator implements PrimitiveIterator.OfInt {
		private final int[] array;
		private int index;

		private IntArrayIterator(int @NotNull [] array) {
			Objects.requireNonNull(array, "array is null");
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			return this.index < this.array.length;
		}

		@Override
		public int nextInt() {
			return this.array[this.index++];
		}
	}

	private static class LongArrayIterator implements PrimitiveIterator.OfLong {
		private final long[] array;
		private int index;

		private LongArrayIterator(long @NotNull [] array) {
			Objects.requireNonNull(array, "array is null");
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			return this.index < this.array.length;
		}

		@Override
		public long nextLong() {
			return this.array[this.index++];
		}
	}

	private static class DoubleArrayIterator implements PrimitiveIterator.OfDouble {
		private final double[] array;
		private int index;

		private DoubleArrayIterator(double @NotNull [] array) {
			Objects.requireNonNull(array, "array is null");
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			return this.index < this.array.length;
		}

		@Override
		public double nextDouble() {
			return this.array[this.index++];
		}
	}
}
