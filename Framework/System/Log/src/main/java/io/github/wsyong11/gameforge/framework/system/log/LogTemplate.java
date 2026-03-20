package io.github.wsyong11.gameforge.framework.system.log;

import io.github.wsyong11.gameforge.framework.annotation.CallerSensitive;
import io.github.wsyong11.gameforge.framework.system.log.templete.HexViewTemplate;
import io.github.wsyong11.gameforge.framework.system.log.templete.TemplateValueProvider;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@UtilityClass
public class LogTemplate {
	private static final TemplateValueProvider NULL_PROVIDER = () -> null;

	private static final String[] TIME_UNITS = {"ns", "µs", "ms", "S"};

	@NotNull
	public static TemplateValueProvider lazy(@NotNull TemplateValueProvider supplier) {
		Objects.requireNonNull(supplier, "supplier is null");
		return supplier;
	}

	@NotNull
	public static TemplateValueProvider lazy(@Nullable Object object) {
		return object != null ? lazy(() -> object) : NULL_PROVIDER;
	}

	@CallerSensitive
	@NotNull
	public static TemplateValueProvider currentStackTrace() {
		Thread thread = Thread.currentThread();
		StackTraceElement[] stackTrace = thread.getStackTrace();
		return () -> Arrays
			.stream(stackTrace)
			.map(e -> "\tat " + e)
			.collect(Collectors.joining("\n"));
	}

	@NotNull
	public static TemplateValueProvider className(@Nullable Object clazz) {
		return clazz != null ? () -> clazz.getClass().getName() : NULL_PROVIDER;
	}

	@NotNull
	public static TemplateValueProvider hex(long value) {
		return () -> "0x" + Long.toHexString(value).toUpperCase(Locale.ROOT);
	}

	@NotNull
	public static TemplateValueProvider bin(long value) {
		return () -> "0b" + Long.toBinaryString(value);
	}

	@NotNull
	public static TemplateValueProvider oct(long value) {
		return () -> "0o" + Long.toOctalString(value);
	}

	@NotNull
	public static TemplateValueProvider formatTime(long time, @Nullable TimeUnit unit) {
		if (unit == null)
			return () -> time + "<unknown unit>";

		return () -> {
			float value = unit.toNanos(time);
			int index = 0;
			while (value > 1000 || index > TIME_UNITS.length) {
				value /= 1000.0F;
				index++;
			}

			return "%.2f%s".formatted(value, TIME_UNITS[index]);
		};
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public static TemplateValueProvider hexView(
		byte @Nullable [] data,
		int offset,
		int length
	) {
		return () -> new HexViewTemplate.Builder(data).length(length).offset(offset).build().getValue();
	}

	@NotNull
	public static TemplateValueProvider hexView(byte @Nullable [] data) {
		return () -> new HexViewTemplate.Builder(data).build().getValue();
	}

	@NotNull
	public static TemplateValueProvider hexView(byte @Nullable [] data, @NotNull Consumer<HexViewTemplate.Builder> builder) {
		Objects.requireNonNull(builder, "builder is null");
		return () -> {
			HexViewTemplate.Builder b = HexViewTemplate.builder(data);
			builder.accept(b);
			return b.build().getValue();
		};
	}
}
