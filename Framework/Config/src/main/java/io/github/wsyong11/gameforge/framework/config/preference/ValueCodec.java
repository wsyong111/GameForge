package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.config.preference.ex.ValueCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.NumberElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ValueCodec<T> {
	ValueCodec<Number> NUMBER_CODEC = new ValueCodec<>() {
		@NotNull
		@Override
		public Element encode(@NotNull Number value) throws ValueCodecException {
			return null;
		}

		@Nullable
		@Override
		public Number decode(@NotNull Element element) throws ValueCodecException {
			if (element instanceof NumberElement num)
				return num.getNumber();

			throw new ValueCodecException("");
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
	};

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	Element encode(@NotNull T value) throws ValueCodecException;

	@Nullable
	T decode(@NotNull Element element) throws ValueCodecException;

	@NotNull
	Set<Class<? extends T>> getSupportTypes();

	default boolean isSupported(@NotNull T value) {
		return true;
	}
}
