package io.github.wsyong11.gameforge.framework.dataflow.element;

import java.util.Objects;

public final class BooleanElement implements Element {
	public static BooleanElement FALSE = new BooleanElement(false);
	public static BooleanElement TRUE = new BooleanElement(true);

	private final boolean value;

	private BooleanElement(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BooleanElement that = (BooleanElement) o;
		return value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.value);
	}

	@Override
	public String toString() {
		return String.valueOf(this.value);
	}
}
