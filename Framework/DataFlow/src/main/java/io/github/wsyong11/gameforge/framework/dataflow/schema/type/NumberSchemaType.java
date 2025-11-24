package io.github.wsyong11.gameforge.framework.dataflow.schema.type;

import io.github.wsyong11.gameforge.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NumberSchemaType extends DefaultValueSchemaType<Number> {
	private final Number minimum;
	private final Number maximum;

	public NumberSchemaType(@Nullable Number defaultValue, @Nullable Number minimum, @Nullable Number maximum) {
		super(defaultValue);
		this.minimum = minimum;
		this.maximum = maximum;
	}

	@Nullable
	public Number getMinimum() {
		return this.minimum;
	}

	@Nullable
	public Number getMaximum() {
		return this.maximum;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		NumberSchemaType that = (NumberSchemaType) o;
		return Objects.equals(this.minimum, that.minimum)
			&& Objects.equals(this.maximum, that.maximum);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.minimum, this.maximum);
	}

	@Override
	public String toString() {
		return "Number{" + StringUtils.joinNonNull(" ",
			StringUtils.formatRange(
				this.minimum == null ? Double.MIN_VALUE : this.minimum.doubleValue(),
				this.maximum == null ? Double.MIN_VALUE : this.maximum.doubleValue()
			),
			this.getDefaultValue() == null ? null : "d" + this.getDefaultValue()
		) + "}";
	}
}
