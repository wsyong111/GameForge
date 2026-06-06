package io.github.wsyong11.gameforge.framework.ex;

import io.github.wsyong11.gameforge.util.number.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 表示在语法解析过程中发生的异常。
 * <p>
 * 该异常通常用于指示输入文本在某个位置存在语法错误，
 * 支持通过索引和文本内容生成可视化的错误定位信息。
 * </p>
 */
public class SyntaxException extends RuntimeException {
	@NotNull
	public static Builder builder(@NotNull String message) {
		Objects.requireNonNull(message, "message is null");
		return new Builder(message);
	}

	@NotNull
	public static SyntaxException of(@NotNull String message, int line, int startCol, int endCol, @Nullable String fileName, @Nullable String sourceLine) {
		Objects.requireNonNull(message, "message is null");
		return new SyntaxException(message, line, startCol, endCol, fileName, sourceLine);
	}

	@NotNull
	public static SyntaxException of(@NotNull String message) {
		Objects.requireNonNull(message, "message is null");
		return new SyntaxException(message, -1, -1, -1, null, null);
	}

	@NotNull
	public static SyntaxException of(@NotNull String message, @NotNull Throwable cause) {
		Objects.requireNonNull(message, "message is null");
		Objects.requireNonNull(cause, "cause is null");
		return new SyntaxException(message, -1, -1, -1, null, null)
			.initCause(cause);
	}

	@NotNull
	public static SyntaxException atIndex(@NotNull String message, int index) {
		Objects.requireNonNull(message, "message is null");
		return atRange(message, index, 1);
	}

	@NotNull
	public static SyntaxException atIndex(@NotNull String message, int index, @NotNull String sourceLine) {
		Objects.requireNonNull(message, "message is null");
		Objects.requireNonNull(sourceLine, "sourceLine is null");
		return atRange(message, index, 1, sourceLine);
	}

	@NotNull
	public static SyntaxException atRange(@NotNull String message, int index, int length) {
		Objects.requireNonNull(message, "message is null");
		return of(message, -1, index, index + length, null, null);
	}

	@NotNull
	public static SyntaxException atRange(@NotNull String message, int index, int length, @NotNull String sourceLine) {
		Objects.requireNonNull(message, "message is null");
		Objects.requireNonNull(sourceLine, "sourceLine is null");
		return of(message, -1, index, index + length, null, sourceLine);
	}

	@Nullable
	private final String fileName;
	private final int line;
	private final int startColumn;
	private final int endColumn;

	@Nullable
	private final String sourceLine;

	public SyntaxException(@NotNull String message, int line, int startColumn, int endColumn, @Nullable String fileName, @Nullable String sourceLine) {
		super(Objects.requireNonNull(message, "message is null"));

		this.fileName = fileName;
		this.line = Math.max(-1, line);
		this.startColumn = Math.max(-1, startColumn);
		this.endColumn = this.startColumn == -1 ? -1 : endColumn;
		this.sourceLine = sourceLine;
	}

	@Override
	public String getMessage() {
		return super.getMessage()
			+ " at "
			+ (this.fileName == null ? "<unknown>" : this.fileName)
			+ ":" + (this.line == -1 ? "?" : this.line + 1)
			+ ":" + (this.startColumn == -1 ? "?" : this.startColumn + 1);
	}

	@Override
	public SyntaxException initCause(@Nullable Throwable cause) {
		return (SyntaxException) super.initCause(cause);
	}

	@Nullable
	public String getFileName() {
		return this.fileName;
	}

	public int getLine() {
		return this.line;
	}

	public int getStartColumn() {
		return this.startColumn;
	}

	public int getEndColumn() {
		return this.endColumn;
	}

	@Nullable
	public String getSourceLine() {
		return this.sourceLine;
	}

	@NotNull
	public String getErrorMessage() {
		return super.getMessage();
	}

	public String getFormattedMessage() {
		/*
SyntaxError: ';' expected
 --> com/example/test:1:1
  |
1 | io.print("Hello world")
  |                        ^
		 */

		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(": ");
		sb.append(super.getMessage());
		sb.append("\n --> ");
		sb.append(this.fileName == null ? "<unknown>" : this.fileName);
		sb.append(':');
		sb.append(this.line == -1 ? "?" : this.line + 1);
		sb.append(':');
		sb.append(this.startColumn == -1 ? "?" : this.startColumn + 1);
		sb.append('\n');

		if (this.sourceLine != null && this.line != -1) {
			int lineNumLength = NumberUtils.digitLength(this.line + 1);
			sb.append(" ".repeat(lineNumLength));
			sb.append(" |\n");
			sb.append(this.line + 1);
			sb.append(" | ");
			sb.append(this.sourceLine);
			sb.append('\n');
			sb.append(" ".repeat(lineNumLength));
			sb.append(" | ");

			if (this.startColumn != -1 && this.endColumn != -1) {
				sb.append(" ".repeat(this.startColumn));
				sb.append("^".repeat(this.endColumn - this.startColumn));
			}
		}

		return sb.toString();
	}

	public static class Builder {
		private final String message;

		@Nullable
		private Throwable cause = null;

		@Nullable
		private String fileName = null;
		private int line = -1;
		private int startColumn = -1;
		private int endColumn = -1;

		@Nullable
		private String sourceLine = null;

		public Builder(@NotNull String message) {
			Objects.requireNonNull(message, "message is null");
			this.message = message;
		}

		@NotNull
		public Builder ofCause(@Nullable Throwable cause) {
			this.cause = cause;
			return this;
		}

		@NotNull
		public Builder ofFile(@Nullable String fileName) {
			this.fileName = fileName;
			return this;
		}

		@NotNull
		public Builder ofLine(int line) {
			this.line = line;
			return this;
		}

		@NotNull
		public Builder ofColumnRange(int start, int end) {
			this.startColumn = start;
			this.endColumn = end;
			return this;
		}

		@NotNull
		public Builder ofColumn(int index) {
			return this.ofColumnRange(index, index + 1);
		}

		@NotNull
		public Builder ofColumn(int index, int length) {
			return this.ofColumnRange(index, index + length);
		}

		@NotNull
		public Builder ofSource(@Nullable String source) {
			this.sourceLine = source;
			return this;
		}

		@NotNull
		public SyntaxException build() {
			SyntaxException exception = new SyntaxException(this.message, this.line, this.startColumn, this.endColumn, this.fileName, this.sourceLine);
			if (this.cause != null)
				exception.initCause(this.cause);

			return exception;
		}
	}
}
