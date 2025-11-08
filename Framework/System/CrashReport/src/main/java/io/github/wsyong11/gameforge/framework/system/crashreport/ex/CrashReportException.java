package io.github.wsyong11.gameforge.framework.system.crashreport.ex;

import io.github.wsyong11.gameforge.framework.system.crashreport.CrashReport;
import org.jetbrains.annotations.NotNull;

public class CrashReportException extends RuntimeException{
	private final CrashReport report;

	public CrashReportException(@NotNull CrashReport report) {
		super("Crash report exception", null, false, false);
		this.report = report;
	}

	@NotNull
	@Override
	public String getMessage() {
		return this.report.getDescription();
	}

	@NotNull
	public CrashReport getReport() {
		return this.report;
	}
}
