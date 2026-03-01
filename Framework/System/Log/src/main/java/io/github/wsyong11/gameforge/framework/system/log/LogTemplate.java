package io.github.wsyong11.gameforge.framework.system.log;

import io.github.wsyong11.gameforge.framework.annotation.CallerSensitive;
import io.github.wsyong11.gameforge.framework.system.log.templete.HexViewTemplate;
import io.github.wsyong11.gameforge.framework.system.log.templete.TemplateValueProvider;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@UtilityClass
public class LogTemplate {
	@NotNull
	public static TemplateValueProvider lazy(@NotNull TemplateValueProvider supplier) {
		Objects.requireNonNull(supplier, "supplier is null");
		return supplier;
	}

	@NotNull
	public static TemplateValueProvider lazy(@Nullable Object object) {
		return lazy(() -> object);
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
