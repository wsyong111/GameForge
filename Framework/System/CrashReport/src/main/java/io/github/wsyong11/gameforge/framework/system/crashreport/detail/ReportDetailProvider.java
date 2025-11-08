package io.github.wsyong11.gameforge.framework.system.crashreport.detail;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ReportDetailProvider {
	void writeDetail(@NotNull CrashReportDetail detail);

	default boolean isAvailable() {
		return true;
	}
}
