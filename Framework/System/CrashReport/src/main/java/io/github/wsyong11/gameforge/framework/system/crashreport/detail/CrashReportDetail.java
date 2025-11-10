package io.github.wsyong11.gameforge.framework.system.crashreport.detail;

import io.github.wsyong11.gameforge.util.exception.ExceptionSupplier;
import io.github.wsyong11.gameforge.util.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public interface CrashReportDetail {
	interface Builder {
		@NotNull
		Builder group(@NotNull String name, @NotNull Consumer<Builder> groupBuilder);

		@NotNull
		Builder detail(@NotNull String label, @Nullable String value);

		@NotNull
		default Builder detail(@NotNull String label, @Nullable ExceptionSupplier<String, Throwable> valueSupplier) {
			Objects.requireNonNull(label, "label is null");
			Objects.requireNonNull(valueSupplier, "valueSupplier is null");

			String value;
			try {
				value = valueSupplier.get();
			} catch (Throwable exception) {
				value = ExceptionUtils.toOneLineString(exception);
			}
			return this.detail(label, value);
		}

		@NotNull
		CrashReportDetail build();
	}
}
