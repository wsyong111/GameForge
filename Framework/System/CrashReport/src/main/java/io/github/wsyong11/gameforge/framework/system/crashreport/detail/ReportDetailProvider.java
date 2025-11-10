package io.github.wsyong11.gameforge.framework.system.crashreport.detail;

import org.jetbrains.annotations.NotNull;

public interface ReportDetailProvider {
	void writeDetail(@NotNull CrashReportDetail.Builder builder);

	@NotNull
	String getTitle();

	default boolean isAvailable() {
		return true;
	}
}
