package io.github.wsyong11.gameforge.framework.dataflow.element.mutable;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.MissingElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class MutableArrayElement {
	private final List<Element> list;

	public MutableArrayElement(@NotNull Collection<Element> list) {
		Objects.requireNonNull(list, "list is null");
		this.list = new ArrayList<>(list);
	}

	public MutableArrayElement() {
		Objects.requireNonNull(list, "list is null");
		this.list = new ArrayList<>(list);
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

		MutableArrayElement that = (MutableArrayElement) o;
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
