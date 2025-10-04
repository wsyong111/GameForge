package io.github.wsyong11.gameforge.framework.lang.ast.parser.error;

import io.github.wsyong11.gameforge.framework.lang.ast.SourceInfo;
import io.github.wsyong11.gameforge.util.collection.list.BoundedArrayList;
import io.github.wsyong11.gameforge.util.collection.list.BoundedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class DefaultCompileReportCollector implements CompileReportCollector {
	private final Map<CompileReportLevel, BoundedList<CompileReport>> reportMap;

	public DefaultCompileReportCollector() {
		this(Map.of());
	}

	public DefaultCompileReportCollector(@NotNull Map<CompileReportLevel, Integer> maxReportCounts) {
		Objects.requireNonNull(maxReportCounts, "maxReportCounts is null");

		this.reportMap = new EnumMap<>(CompileReportLevel.class);

		for (CompileReportLevel level : CompileReportLevel.values()) {
			int maxCount = maxReportCounts.getOrDefault(level, 100);
			this.reportMap.put(level, new BoundedArrayList<>(maxCount));
		}
	}

	@Override
	public void report(@NotNull SourceInfo source, int pointerStartIndex, @NotNull CompileReportLevel level, @NotNull String message) {
		Objects.requireNonNull(source, "source is null");
		Objects.requireNonNull(level, "level is null");
		Objects.requireNonNull(message, "message is null");

		BoundedList<CompileReport> reports = this.reportMap.get(level);
		if (reports.isFull())
			return;

		SimpleCompileReport report = new SimpleCompileReport(source, pointerStartIndex, level, message, null);
		reports.add(report);
	}

	@Override
	public int getMaxReportCount(@NotNull CompileReportLevel level) {
		Objects.requireNonNull(level, "level is null");
		return this.reportMap.get(level).getMaxSize();
	}

	@Override
	public int getReportCount() {
		return this.reportMap
			.values()
			.stream()
			.mapToInt(List::size)
			.sum();
	}

	@Override
	public int getReportCount(@NotNull CompileReportLevel level) {
		Objects.requireNonNull(level, "level is null");
		return this.reportMap.get(level).size();
	}

	@Override
	public boolean hasReports(@NotNull CompileReportLevel level) {
		Objects.requireNonNull(level, "level is null");
		return !this.reportMap.get(level).isEmpty();
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<CompileReport> getReports(@NotNull CompileReportLevel level) {
		Objects.requireNonNull(level, "level is null");
		return List.copyOf(this.reportMap.get(level));
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<CompileReport> getAllReports() {
		return this.reportMap
			.values()
			.stream()
			.flatMap(Collection::stream)
			.toList();
	}

	@Override
	public void merge(@NotNull CompileReportCollector collector) {
		Objects.requireNonNull(collector, "collector is null");

		for (CompileReportLevel level : CompileReportLevel.values()) {
			BoundedList<CompileReport> reports = this.reportMap.get(level);
			if (reports.isFull())
				continue;

			for (CompileReport report : collector.getReports(level)) {
				reports.add(report);

				if (reports.isFull())
					break;
			}
		}
	}

	@NotNull
	@Override
	public String formatAll() {
		List<CompileReport> reports = this.getAllReportsSorted();
		if (reports.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		for (CompileReport report : reports) {
			report.format(sb);
			sb.append('\n');
		}

		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}
