package io.github.wsyong11.gameforge.framework.dataflow.codec.codec;

import io.github.wsyong11.gameforge.framework.dataflow.element.BooleanElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.NumberElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.StringElement;
import io.github.wsyong11.gameforge.framework.ex.CodecException;

import java.util.List;

public class CommonCodecs {
	public static final Codec<String> STRING_CODEC = SimpleCodec
		.<String>builder()
		.encoder((ctx, value, type) -> Element.string(value))
		.decoder((ctx, element, type) -> {
			if (element instanceof StringElement string)
				return string.getValue();
			throw new CodecException();
		})
		.supportTypes(String.class)
		.supportElement(e -> e instanceof StringElement)
		.build();

	public static final Codec<Number> NUMBER_CODEC = SimpleCodec
		.<Number>builder()
		.encoder((ctx, value, type) -> Element.number(value))
		.decoder((ctx, element, type) -> {
			if (!(element instanceof NumberElement num))
				throw new CodecException();

			Number number = num.getNumber();
			if (Byte.class.equals(type)) return number.byteValue();
			if (Short.class.equals(type)) return number.shortValue();
			if (Integer.class.equals(type)) return number.intValue();
			if (Long.class.equals(type)) return number.longValue();
			if (Float.class.equals(type)) return number.floatValue();
			if (Double.class.equals(type)) return number.doubleValue();

			throw new CodecException("Cannot cast number to " + type.getSimpleName());
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

	public static final Codec<Boolean> BOOLEAN_CODEC = SimpleCodec
		.<Boolean>builder()
		.encoder((ctx, value, type) -> Element.bool(value))
		.decoder((ctx, element, type) -> {
			if (element instanceof BooleanElement bool)
				return bool.getValue();
			throw new CodecException();
		})
		.supportTypes(Boolean.class)
		.supportElement(e -> e instanceof BooleanElement)
		.build();

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final Codec<Enum<?>> ENUM_CODEC = SimpleCodec
		.<Enum<?>>builder()
		.encoder((ctx, value, type) -> ctx.encode("@enum:" + value.name(), String.class))
		.decoder((ctx, element, type) -> {
			String value = (String) ctx.decode(element, String.class);
			if (value == null || !value.startsWith("@enum:"))
				throw new CodecException("Cannot decode to enum " + type + ", value is not available enum value");

			String name = value.substring(6);
			try {
				return Enum.valueOf((Class) type, name);
			} catch (IllegalArgumentException e) {
				throw new CodecException("Cannot parse '" + name + "' to enum " + type);
			}
		})
		.supportType(Class::isEnum)
		.supportElement(e -> e instanceof StringElement)
		.build();

	public static final List<Codec<?>> CODECS = List.of(
		STRING_CODEC,
		NUMBER_CODEC,
		BOOLEAN_CODEC,
		ENUM_CODEC
	);
}
