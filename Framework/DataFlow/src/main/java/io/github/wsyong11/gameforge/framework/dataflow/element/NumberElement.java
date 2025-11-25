package io.github.wsyong11.gameforge.framework.dataflow.element;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NumberElement implements Element, Comparable<NumberElement> {
	private final Number number;

	public NumberElement(int number) {
		this.number = number;
	}

	public NumberElement(long number) {
		this.number = number;
	}

	public NumberElement(float number) {
		this.number = number;
	}

	public NumberElement(double number) {
		this.number = number;
	}

	@NotNull
	public Number getNumber() {
		return this.number;
	}

	public int getAsInt() {
		return this.number.intValue();
	}

	public long getAsLong() {
		return this.number.longValue();
	}

	public float getAsFloat() {
		return this.number.floatValue();
	}

	public double getAsDouble() {
		return this.number.doubleValue();
	}

	@Override
	public int compareTo(@NotNull NumberElement o) {
		Objects.requireNonNull(o, "o is null");
		return Double.compare(this.number.doubleValue(), o.number.doubleValue());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NumberElement that = (NumberElement) o;
		return Double.compare(this.number.doubleValue(), that.number.doubleValue()) == 0;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(this.number.doubleValue());
	}

	@Override
	public String toString() {
		return this.number.toString();
	}
}
