package io.github.wsyong11.gameforge.framework.dataflow.element.mutable;

import io.github.wsyong11.gameforge.framework.dataflow.element.ArrayElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.MissingElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.NullElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractArrayElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class MutableArrayElement extends AbstractArrayElement<MutableElement> implements MutableElement {
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
	public List<MutableElement> asList() {
		return this.list;
	}

	@NotNull
	@Override
	public Element asElement() {
		return new ArrayElement(this.list
			.stream()
			.map(MutableElement::asElementSafe)
			.toList());
	}
}
