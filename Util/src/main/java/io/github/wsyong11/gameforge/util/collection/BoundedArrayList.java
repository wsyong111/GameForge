package io.github.wsyong11.gameforge.util.collection;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.IntFunction;

@SuppressWarnings("unchecked")
public class BoundedArrayList<T> extends AbstractList<T> implements BoundedList<T> {
	private final int maxSize;
	private final Object[] elements;

	private int usedSize;

	public BoundedArrayList(int maxSize) {
		if (maxSize < 0)
			throw new IllegalArgumentException("maxSize cannot be negative");

		this.maxSize = maxSize;
		this.elements = new Object[maxSize];
		this.usedSize = 0;
	}

	private void checkIndex(int index) {
		if (index < 0 || index >= this.usedSize)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + usedSize);
	}

	@Override
	public int getMaxSize() {
		return this.maxSize;
	}

	@Override
	public T get(int index) {
		this.checkIndex(index);
		return (T) elements[index];
	}

	@Override
	public T set(int index, T element) {
		checkIndex(index);
		T old = (T) elements[index];
		this.elements[index] = element;
		return old;
	}

	@Override
	public boolean add(T element) {
		this.add(this.usedSize, element);
		return true;
	}

	@Override
	public void add(int index, T element) {
		if (this.usedSize >= this.maxSize)
			throw new IllegalStateException("BoundedArrayList capacity exceeded");

		if (index < 0 || index > this.usedSize)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.usedSize);

		System.arraycopy(this.elements, index, this.elements, index + 1, this.usedSize - index);
		this.elements[index] = element;
		this.usedSize++;
	}

	@Override
	public boolean remove(Object o) {
		if (this.usedSize == 0)
			return false;

		for (int i = 0; i < this.usedSize; i++) {
			if (Objects.equals(this.elements[i], o)) {
				this.remove(i);
				return true;
			}
		}

		return false;
	}

	@Override
	public T remove(int index) {
		this.checkIndex(index);

		T old = (T) this.elements[index];
		int numMoved = this.usedSize - index - 1;
		if (numMoved > 0)
			System.arraycopy(this.elements, index + 1, this.elements, index, numMoved);

		this.elements[--this.usedSize] = null;
		return old;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.usedSize; i++)
			this.elements[i] = null;
		this.usedSize = 0;
	}

	@Override
	public Object @NotNull [] toArray() {
		if (this.usedSize == 0)
			return ArrayUtils.EMPTY_OBJECT_ARRAY;

		Object[] array = new Object[this.usedSize];
		System.arraycopy(this.elements, 0, array, 0, this.usedSize);
		return array;
	}


	@Override
	public <V> V @NotNull [] toArray(V @NotNull [] a) {
		Objects.requireNonNull(a, "a is null");

		V[] array = a.length >= this.usedSize
			? a
			: (V[]) Array.newInstance(a.getClass().getComponentType(), this.usedSize);

		for (int i = 0; i < this.usedSize; i++)
			array[i] = (V) this.elements[i];

		Arrays.fill(array, this.usedSize, array.length, null);

		return array;
	}

	@Override
	public <V> V[] toArray(@NotNull IntFunction<V[]> generator) {
		Objects.requireNonNull(generator, "generator is null");

		V[] array = generator.apply(this.usedSize);
		for (int i = 0; i < this.usedSize; i++)
			array[i] = (V) this.elements[i];
		return array;
	}

	@Override
	public int size() {
		return this.usedSize;
	}
}
