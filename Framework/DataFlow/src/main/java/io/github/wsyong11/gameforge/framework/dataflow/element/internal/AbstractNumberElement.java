package io.github.wsyong11.gameforge.framework.dataflow.element.internal;

import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseNumberElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractNumberElement<THIS extends AbstractNumberElement<THIS>> implements BaseNumberElement<THIS> {
	private Number number;

	public AbstractNumberElement(@NotNull Number number) {
		Objects.requireNonNull(number, "number is null");
		this.number = number;
	}

	protected void set(@NotNull Number number) {
		Objects.requireNonNull(number, "number is null");
		this.number = number;
	}

	@Override
	@NotNull
	public Number getNumber() {
		return this.number;
	}

	@Override
	public int getAsInt() {
		return this.number.intValue();
	}

	@Override
	public long getAsLong() {
		return this.number.longValue();
	}

	@Override
	public float getAsFloat() {
		return this.number.floatValue();
	}

	@Override
	public double getAsDouble() {
		return this.number.doubleValue();
	}

	@Override
	public byte getAsByte() {
		return this.number.byteValue();
	}

	@Override
	public short getAsShort() {
		return this.number.shortValue();
	}

	@Override
	public int compareTo(@NotNull THIS o) {
		Objects.requireNonNull(o, "o is null");
		return Double.compare(this.number.doubleValue(), o.getAsDouble());
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractNumberElement<?> that = (AbstractNumberElement<?>) o;
		return Double.compare(this.number.doubleValue(), that.number.doubleValue()) == 0;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(this.number.doubleValue());
	}

	@NotNull
	@Override
	public String toString() {
		return this.number.toString();
	}
}
