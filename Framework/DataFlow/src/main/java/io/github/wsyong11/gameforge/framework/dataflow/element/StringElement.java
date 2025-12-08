package io.github.wsyong11.gameforge.framework.dataflow.element;

import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractStringElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableStringElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StringElement extends AbstractStringElement<StringElement> implements Element {
	public StringElement(@NotNull String value) {
		super(value);
	}

	@Nullable
	@Override
	public MutableStringElement asMutable() {
		return new MutableStringElement(this.getValue());
	}
}
