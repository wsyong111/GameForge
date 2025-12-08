package io.github.wsyong11.gameforge.framework.dataflow.element.mutable;

import io.github.wsyong11.gameforge.framework.dataflow.element.StringElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractStringElement;
import org.jetbrains.annotations.NotNull;

public final class MutableStringElement extends AbstractStringElement<MutableStringElement> implements MutableElement {
	public MutableStringElement(@NotNull String value) {
		super(value);
	}

	@NotNull
	public MutableStringElement setValue(@NotNull String value) {
		this.set(value);
		return this;
	}

	@NotNull
	@Override
	public StringElement asElement() {
		return new StringElement(this.getValue());
	}
}
