package io.github.wsyong11.gameforge.framework.dataflow.element.mutable;

import io.github.wsyong11.gameforge.framework.dataflow.element.NumberElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractNumberElement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MutableNumberElement extends AbstractNumberElement<MutableNumberElement> implements MutableElement {
	public MutableNumberElement(@NotNull Number number) {
		super(number);
	}

	@NotNull
	public MutableNumberElement setValue(@NotNull Number number) {
		Objects.requireNonNull(number, "number is null");
		this.set(number);
		return this;
	}

	@NotNull
	@Override
	public NumberElement asElement() {
		return new NumberElement(this.getNumber());
	}
}
