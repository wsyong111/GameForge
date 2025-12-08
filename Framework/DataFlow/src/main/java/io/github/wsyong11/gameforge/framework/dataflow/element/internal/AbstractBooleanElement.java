package io.github.wsyong11.gameforge.framework.dataflow.element.internal;

import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseBooleanElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractBooleanElement implements BaseBooleanElement {
	private boolean value;

	protected AbstractBooleanElement(boolean value) {
		this.value = value;
	}

	@Override
	public boolean getValue() {
		return this.value;
	}

	protected void set(boolean value) {
		this.value = value;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractBooleanElement that = (AbstractBooleanElement) o;
		return value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.value);
	}

	@NotNull
	@Override
	public String toString() {
		return String.valueOf(this.value);
	}
}
