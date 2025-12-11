package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.config.preference.ex.ValueCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.BooleanElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.NumberElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.StringElement;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

@UtilityClass
public class ValueCodecs {
	public static final ValueCodec<String> STRING_CODEC = new ValueCodec<>() {
		@NotNull
		@Override
		public Element encode(@NotNull String value, @NotNull Class<? extends String> type) {
			Objects.requireNonNull(value, "value is null");
			return Element.string(value);
		}

		@NotNull
		@Override
		public String decode(@NotNull Element element, @NotNull Class<? extends String> type) throws ValueCodecException {
			Objects.requireNonNull(element, "element is null");

			if (element instanceof StringElement string)
				return string.getValue();

			throw new ValueCodecException("Cannot decode to string " + element);
		}

		@NotNull
		@Override
		public Set<Class<? extends String>> getSupportTypes() {
			return Set.of(String.class);
		}

		@Override
		public boolean isSupportedElement(@NotNull Element value) {
			Objects.requireNonNull(value, "value is null");
			return value instanceof StringElement;
		}
	};

	public static final ValueCodec<Number> NUMBER_CODEC = new ValueCodec<>() {
		@NotNull
		@Override
		public Element encode(@NotNull Number value, @NotNull Class<? extends Number> type) {
			Objects.requireNonNull(value, "value is null");
			return new NumberElement(value);
		}

		@NotNull
		@Override
		public Number decode(@NotNull Element element, @NotNull Class<? extends Number> type) throws ValueCodecException {
			Objects.requireNonNull(element, "element is null");

			if (!(element instanceof NumberElement num))
				throw new ValueCodecException("Cannot decode to number " + element);

			Number number = num.getNumber();
			if (Byte.class.equals(type)) return number.byteValue();
			if (Short.class.equals(type)) return number.shortValue();
			if (Integer.class.equals(type)) return number.intValue();
			if (Long.class.equals(type)) return number.longValue();
			if (Float.class.equals(type)) return number.floatValue();
			if (Double.class.equals(type)) return number.doubleValue();

			throw new ValueCodecException("Cannot cast number to " + type.getSimpleName());
		}

		@NotNull
		@Override
		public Set<Class<? extends Number>> getSupportTypes() {
			return Set.of(
				Byte.class,
				Short.class,
				Integer.class,
				Long.class,
				Float.class,
				Double.class
			);
		}

		@Override
		public boolean isSupportedElement(@NotNull Element value) {
			Objects.requireNonNull(value, "value is null");
			return value instanceof NumberElement;
		}
	};

	public static final ValueCodec<Boolean> BOOLEAN_CODEC = new ValueCodec<>() {
		@NotNull
		@Override
		public Element encode(@NotNull Boolean value, @NotNull Class<? extends Boolean> type) throws ValueCodecException {
			Objects.requireNonNull(value, "value is null");
			return Element.bool(value);
		}

		@NotNull
		@Override
		public Boolean decode(@NotNull Element element, @NotNull Class<? extends Boolean> type) throws ValueCodecException {
			Objects.requireNonNull(element, "element is null");

			if (element instanceof BooleanElement bool)
				return bool.getValue();

			throw new ValueCodecException("Cannot decode to boolean " + element);
		}

		@NotNull
		@Override
		public Set<Class<? extends Boolean>> getSupportTypes() {
			return Set.of(Boolean.class);
		}

		@Override
		public boolean isSupportedElement(@NotNull Element value) {
			Objects.requireNonNull(value, "value is null");
			return value instanceof BooleanElement;
		}
	};
}
