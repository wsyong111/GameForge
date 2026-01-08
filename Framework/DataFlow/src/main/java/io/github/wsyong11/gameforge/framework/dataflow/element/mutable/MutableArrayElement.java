package io.github.wsyong11.gameforge.framework.dataflow.element.mutable;

import io.github.wsyong11.gameforge.framework.dataflow.element.ArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractArrayElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public final class MutableArrayElement extends AbstractArrayElement<MutableElement> implements MutableElement {
	public MutableArrayElement() {
		super(new ArrayList<>());
	}

	public MutableArrayElement(@NotNull Collection<MutableElement> list) {
		super(new ArrayList<>(Objects.requireNonNull(list, "list is null")));
	}

	@NotNull
	public MutableArrayElement add(@Nullable MutableElement element) {
		this.list.add(element);
		return this;
	}

	@NotNull
	public MutableArrayElement clear() {
		this.list.clear();
		return this;
	}

	@NotNull
	public MutableArrayElement remove(@Nullable MutableElement element) {
		this.list.remove(element);
		return this;
	}

	@NotNull
	@Override
	public ArrayElement asElement() {
		return new ArrayElement(this.list
			.stream()
			.map(MutableElement::asElementSafe)
			.toList());
	}

	@NotNull
	@Override
	public MutableArrayElement copy() {
		return new MutableArrayElement(this.list);
	}

	@NotNull
	@Override
	public MutableArrayElement deepCopy() {
		return new MutableArrayElement(this.list
			.stream()
			.map(MutableElement::deepCopy)
			.toList());
	}
}
