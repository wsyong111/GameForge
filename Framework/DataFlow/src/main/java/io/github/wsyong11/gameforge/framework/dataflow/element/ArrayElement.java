package io.github.wsyong11.gameforge.framework.dataflow.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ArrayElement implements Element, Iterable<Element> {
	private final List<Element> list;

	public ArrayElement(@NotNull List<Element> list) {
		Objects.requireNonNull(list, "list is null");
		this.list = List.copyOf(list);
	}

	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	public int size() {
		return this.list.size();
	}

	@NotNull
	public Element get(int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException(index);

		if (index >= this.list.size())
			return MissingElement.INSTANCE;

		return this.list.get(index);
	}

	@NotNull
	@UnmodifiableView
	public List<Element> asList() {
		return this.list;
	}

	@NotNull
	@Override
	public Iterator<Element> iterator() {
		return this.list.iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArrayElement that = (ArrayElement) o;
		return Objects.equals(this.list, that.list);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.list);
	}

	@Override
	public String toString() {
		return this.list.toString();
	}
}
