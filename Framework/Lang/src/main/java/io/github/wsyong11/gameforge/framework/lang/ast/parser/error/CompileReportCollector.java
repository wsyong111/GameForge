package io.github.wsyong11.gameforge.framework.lang.ast.parser.error;

import io.github.wsyong11.gameforge.framework.lang.ast.SimpleSourceInfo;
import io.github.wsyong11.gameforge.framework.lang.ast.SourceInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public interface CompileReportCollector {
	void report(@NotNull SourceInfo source, int pointerStartIndex, @NotNull CompileReportLevel level, @NotNull String message);

	default void report(@NotNull SourceInfo source, @NotNull CompileReportLevel level, @NotNull String message) {
		Objects.requireNonNull(source, "source is null");
		Objects.requireNonNull(level, "level is null");
		Objects.requireNonNull(message, "message is null");
		this.report(source, source.getCol(), level, message);
	}

	int getMaxReportCount(@NotNull CompileReportLevel level);

	int getReportCount();

	int getReportCount(@NotNull CompileReportLevel level);

	boolean hasReports(@NotNull CompileReportLevel level);

	@NotNull
	@Unmodifiable
	List<CompileReport> getReports(@NotNull CompileReportLevel level);

	@NotNull
	@Unmodifiable
	List<CompileReport> getAllReports();

	@NotNull
	@Unmodifiable
	default List<CompileReport> getAllReportsSorted() {
		return this
			.getAllReports()
			.stream()
			.sorted(Comparator
				.comparing((CompileReport r) -> r.getSource().getFileName())
				.thenComparingInt(r -> r.getSource().getRow())
				.thenComparingInt(r -> r.getSource().getCol())
				.thenComparingInt(r -> -r.getLevel().toInt()))
			.toList();
	}

	void merge(@NotNull CompileReportCollector collector);

	@NotNull
	String formatAll();
}
