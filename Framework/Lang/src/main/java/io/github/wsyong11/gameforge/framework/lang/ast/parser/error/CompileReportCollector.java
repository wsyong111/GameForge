package io.github.wsyong11.gameforge.framework.lang.ast.parser.error;

import io.github.wsyong11.gameforge.framework.lang.ast.SourceInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Comparator;
import java.util.List;

public interface CompileReportCollector {
	void report(@NotNull SourceInfo source, int pointerStartIndex, @NotNull CompileReportLevel level, @NotNull String message);

	int getMaxReportCount(@NotNull CompileReportLevel level);

	int getReportCount();

	int getReportCount(@NotNull CompileReportLevel level);

	boolean hasReports(@NotNull CompileReportLevel level);

	@NotNull
	@UnmodifiableView
	List<CompileReport> getReports(@NotNull CompileReportLevel level);

	@NotNull
	@UnmodifiableView
	List<CompileReport> getAllReports();

	@NotNull
	@UnmodifiableView
	default List<CompileReport> getAllReportsSorted() {
		return this
			.getAllReports()
			.stream()
			.sorted(Comparator
				.comparing((CompileReport r) -> r.getSource().getFile().toString())
				.thenComparingInt(r -> r.getSource().getRow())
				.thenComparingInt(r -> r.getSource().getCol())
				.thenComparingInt(r -> -r.getLevel().toInt()))
			.toList();
	}

	void merge(@NotNull CompileReportCollector collector);

	@NotNull
	String formatAll();
}
