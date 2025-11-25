package io.github.wsyong11.gameforge.framework.dataflow.element;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StringElement implements Element, Comparable<StringElement> {
	private final String value;

	public StringElement(@NotNull String value) {
		Objects.requireNonNull(value, "value is null");
		this.value = value;
	}

	@NotNull
	public String getValue() {
		return this.value;
	}

	@Override
	public int compareTo(@NotNull StringElement o) {
		Objects.requireNonNull(o, "o is null");
		return this.value.compareTo(o.value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StringElement that = (StringElement) o;
		return Objects.equals(this.value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.value);
	}

	@Override
	public String toString() {
		return this.value.toString();
	}
}
