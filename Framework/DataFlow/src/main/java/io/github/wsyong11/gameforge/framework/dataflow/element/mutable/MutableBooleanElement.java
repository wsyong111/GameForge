package io.github.wsyong11.gameforge.framework.dataflow.element.mutable;

import io.github.wsyong11.gameforge.framework.dataflow.element.BooleanElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractBooleanElement;
import org.jetbrains.annotations.NotNull;

public final class MutableBooleanElement extends AbstractBooleanElement implements MutableElement {
	public MutableBooleanElement(boolean value) {
		super(value);
	}

	@NotNull
	public MutableBooleanElement setValue(boolean value) {
		this.set(value);
		return this;
	}

	@NotNull
	@Override
	public BooleanElement asElement() {
		return BooleanElement.of(this.getValue());
	}

	@NotNull
	@Override
	public MutableBooleanElement copy() {
		return new MutableBooleanElement(this.getValue());
	}

	@NotNull
	@Override
	public MutableBooleanElement deepCopy() {
		return new MutableBooleanElement(this.getValue());
	}
}
