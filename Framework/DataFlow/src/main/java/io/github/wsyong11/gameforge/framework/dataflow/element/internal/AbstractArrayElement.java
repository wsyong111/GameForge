package io.github.wsyong11.gameforge.framework.dataflow.element.internal;

import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class AbstractArrayElement<E extends BaseElement> implements BaseArrayElement<E> {
	protected final List<E> list;

	protected AbstractArrayElement(@NotNull List<E> list) {
		Objects.requireNonNull(list, "list is null");
		this.list = list;
	}

	@Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	@Nullable
	public E get(int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException(index);

		if (index >= this.list.size())
			return null;

		return this.list.get(index);
	}

	@NotNull
	@Override
	public List<E> asList() {
		return this.list;
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return this.list.iterator();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		AbstractArrayElement<?> that = (AbstractArrayElement<?>) o;
		return Objects.equals(this.list, that.list);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.list);
	}

	@NotNull
	@Override
	public String toString() {
		return this.list.toString();
	}
}
