package io.github.wsyong11.gameforge.framework.lang.ast.parser.error;

import io.github.wsyong11.gameforge.framework.lang.ast.SourceInfo;
import org.jetbrains.annotations.NotNull;

public interface CompileReport {
	@NotNull
	SourceInfo getSource();

	int getPointerIndex();

	@NotNull
	CompileReportLevel getLevel();

	@NotNull
	String getMessage();

	/**
	 * 格式化报告为字符串
	 * <blockquote><pre>
	 * path/to/file.java: 3:14 ERROR: Floating point text format error
	 *  3 | float x = 3.0e;
	 *    |           ^
	 *    | NOTE: did you mean '3.0f'?
	 * </pre></blockquote><p>
	 *
	 * @param sb 字符串构建器，结尾不包含换行符
	 */
	void format(@NotNull StringBuilder sb);

	@NotNull
	default String format() {
		StringBuilder sb = new StringBuilder();
		this.format(sb);
		return sb.toString();
	}
}
