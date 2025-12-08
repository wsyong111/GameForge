package io.github.wsyong11.gameforge.framework.dataflow.element;

import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractNumberElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableNumberElement;
import org.jetbrains.annotations.NotNull;

public final class NumberElement extends AbstractNumberElement<NumberElement> implements Element {
	public NumberElement(@NotNull Number number) {
		super(number);
	}

	@NotNull
	@Override
	public MutableNumberElement asMutable() {
		return new MutableNumberElement(this.getNumber());
	}
}
