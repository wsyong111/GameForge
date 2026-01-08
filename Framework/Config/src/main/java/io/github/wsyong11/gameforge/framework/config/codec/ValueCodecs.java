package io.github.wsyong11.gameforge.framework.config.codec;

import com.google.common.reflect.TypeToken;
import io.github.wsyong11.gameforge.framework.config.ex.ValueCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@UtilityClass
public class ValueCodecs {
	public static final ValueCodec<String> STRING_CODEC = SimpleValueCodec
		.<String>builder()
		.encoder((ctx, value, type) -> Element.string(value))
		.decoder((ctx, element, type) -> {
			if (element instanceof StringElement string)
				return string.getValue();

			throw new ValueCodecException("Cannot decode to string " + element);
		})
		.supportTypes(String.class)
		.supportElement(e -> e instanceof StringElement)
		.build();

	public static final ValueCodec<Number> NUMBER_CODEC = SimpleValueCodec
		.<Number>builder()
		.encoder((ctx, value, type) -> Element.number(value))
		.decoder((ctx, element, type) -> {
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
		})
		.supportTypes(
			Byte.class,
			Short.class,
			Integer.class,
			Long.class,
			Float.class,
			Double.class)
		.supportElement(e -> e instanceof NumberElement)
		.build();

	public static final ValueCodec<Boolean> BOOLEAN_CODEC = SimpleValueCodec
		.<Boolean>builder()
		.encoder((ctx, value, type) -> Element.bool(value))
		.decoder((ctx, element, type) -> {
			if (element instanceof BooleanElement bool)
				return bool.getValue();

			throw new ValueCodecException("Cannot decode to boolean " + element);
		})
		.supportTypes(Boolean.class)
		.supportElement(e -> e instanceof BooleanElement)
		.build();

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final ValueCodec<Enum<?>> ENUM_CODEC = SimpleValueCodec
		.<Enum<?>>builder()
		.encoder((ctx, value, type) -> ctx.encode("@enum:" + value.name(), String.class))
		.decoder((ctx, element, type) -> {
			String value = ctx.decode(element, String.class);
			if (value == null || !value.startsWith("@enum:"))
				throw new ValueCodecException("Cannot decode to enum " + type + ", value is not available enum value");

			String name = value.substring(6);
			try {
				return Enum.valueOf((Class) type, name);
			} catch (IllegalArgumentException e) {
				throw new ValueCodecException("Cannot parse '" + name + "' to enum " + type);
			}
		})
		.supportType(Class::isEnum)
		.supportElement(e -> e instanceof StringElement)
		.build();

	public static final List<ValueCodec<?>> CODECS = List.of(
		STRING_CODEC,
		NUMBER_CODEC,
		BOOLEAN_CODEC,
		ENUM_CODEC
	);

	public static void fill(@NotNull CodecMap map) {
		Objects.requireNonNull(map, "map is null");
		CODECS.forEach(map::add);
	}
}
