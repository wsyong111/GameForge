package io.github.wsyong11.gameforge.framework.lang.ast.parser.error;

import io.github.wsyong11.gameforge.framework.lang.ast.SourceInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

public class SimpleCompileReport implements CompileReport {
	private final SourceInfo source;
	private final int pointerIndex;
	private final CompileReportLevel level;
	private final String message;

	@Nullable
	private final String note;

	public SimpleCompileReport(@NotNull SourceInfo source, int pointerIndex, @NotNull CompileReportLevel level, @NotNull String message, @Nullable String note) {
		Objects.requireNonNull(source, "source is null");
		Objects.requireNonNull(level, "level is null");
		Objects.requireNonNull(message, "message is null");

		this.source = source;
		this.pointerIndex = Math.max(pointerIndex, -1);
		this.level = level;
		this.message = message;
		this.note = note;
	}

	@NotNull
	@Override
	public SourceInfo getSource() {
		return this.source;
	}

	@Override
	public int getPointerIndex() {
		return this.pointerIndex;
	}

	@NotNull
	@Override
	public CompileReportLevel getLevel() {
		return this.level;
	}

	@NotNull
	@Override
	public String getMessage() {
		return this.message;
	}

	@Nullable
	@Override
	public String getNote() {
		return this.note;
	}

	@Override
	public void format(@NotNull StringBuilder sb) {
		Objects.requireNonNull(sb, "sb is null");

		sb.append(this.source.getFileName())
		  .append(": ")
		  .append(this.source.getRow())
		  .append(':')
		  .append(this.source.getCol())
		  .append(' ')
		  .append(this.level.toString().toUpperCase(Locale.ROOT))
		  .append(": ")
		  .append(this.message)
		  .append('\n');

		String lineString = String.valueOf(this.source.getRow());

		sb.append(' ')
		  .append(lineString)
		  .append(" | ")
		  .append(this.source.getSource());

		if (this.pointerIndex != -1) {
			sb.append('\n')
				.append(" ".repeat(lineString.length() + 2))
			  .append("| ")
			  .append(" ".repeat(this.pointerIndex - 1))
			  .append('^');
		}

		if (this.note != null) {
			sb.append(" ".repeat(lineString.length() + 2))
			  .append("| NOTE: ")
			  .append(this.note);
		}
	}
}
