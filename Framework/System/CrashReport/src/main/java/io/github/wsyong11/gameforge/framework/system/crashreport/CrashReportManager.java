package io.github.wsyong11.gameforge.framework.system.crashreport;

import io.github.wsyong11.gameforge.framework.annotation.CallerSensitive;
import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.framework.system.crashreport.detail.ReportDetailProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Future;

public interface CrashReportManager {
	void registerDetailProvider(@NotNull ReportDetailProvider provider);

	void unregisterDetailProvider(@NotNull ReportDetailProvider provider);

	@CallerSensitive
	@ThreadSensitive
	@NotNull
	Future<CrashReport> dump();
}
