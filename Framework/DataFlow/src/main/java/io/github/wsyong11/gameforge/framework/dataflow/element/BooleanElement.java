package io.github.wsyong11.gameforge.framework.dataflow.element;

import io.github.wsyong11.gameforge.framework.dataflow.element.internal.AbstractBooleanElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableBooleanElement;
import org.jetbrains.annotations.NotNull;

public final class BooleanElement extends AbstractBooleanElement implements Element {
	public static BooleanElement FALSE = new BooleanElement(false);
	public static BooleanElement TRUE = new BooleanElement(true);

	private BooleanElement(boolean value) {
		super(value);
	}

	@NotNull
	public static BooleanElement of(boolean value) {
		return value ? TRUE : FALSE;
	}

	@NotNull
	@Override
	public MutableBooleanElement asMutable() {
		return new MutableBooleanElement(this.getValue());
	}
}
