package io.github.wsyong11.gameforge.framework.dataflow.element;

import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ArrayElement extends AbstractArrayElement<Element> implements Element {
	public ArrayElement(@NotNull Collection<Element> list) {
		super(List.copyOf(Objects.requireNonNull(list, "list is null")));
	}

	@NotNull
	@Override
	public Element get(int index) {
		return Objects.requireNonNullElse(super.get(index), MissingElement.INSTANCE);
	}

	@NotNull
	@UnmodifiableView
	@Override
	public List<Element> asList() {
		return this.list;
	}

	@NotNull
	@Override
	public MutableElement asMutable() {
		return new MutableArrayElement(this.list
			.stream()
			.map(Element::asMutable)
			.toList());
	}
}
