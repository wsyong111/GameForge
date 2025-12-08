package io.github.wsyong11.gameforge.framework.dataflow.element.internal;

import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseStringElement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractStringElement<THIS extends AbstractStringElement<THIS>> implements BaseStringElement<THIS> {
	private String value;

	public AbstractStringElement(@NotNull String value) {
		Objects.requireNonNull(value, "value is null");
		this.value = value;
	}

	protected void set(@NotNull String value) {
		Objects.requireNonNull(value, "value is null");
		this.value = value;
	}

	@NotNull
	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public int compareTo(@NotNull THIS o) {
		Objects.requireNonNull(o, "o is null");
		return this.getValue().compareTo(o.getValue());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractStringElement<?> that = (AbstractStringElement<?>) o;
		return Objects.equals(this.value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.value);
	}

	@NotNull
	@Override
	public String toString() {
		return this.value;
	}
}
