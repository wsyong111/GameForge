package io.github.wsyong11.gameforge.framework.ex;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 表示在语法解析过程中发生的异常。
 * <p>
 * 该异常通常用于指示输入文本在某个位置存在语法错误，
 * 支持通过索引和文本内容生成可视化的错误定位信息。
 * </p>
 */
public class SyntaxException extends RuntimeException {
	/**
	 * 使用指定的错误信息构造一个语法异常。
	 *
	 * @param message 错误信息，不可为 null
	 */
	public SyntaxException(@NotNull String message) {
		super(message);
	}

	/**
	 * 使用指定的错误信息和原因构造一个语法异常。
	 *
	 * @param message 错误信息，不可为 null
	 * @param e       导致该异常的原始异常，不可为 null
	 */
	public SyntaxException(@NotNull String message, @NotNull Throwable e) {
		super(message, e);
	}

	/**
	 * 使用错误信息和错误位置索引构造一个语法异常。
	 * <p>
	 * 会自动在错误信息前添加位置标识。
	 * </p>
	 *
	 * @param message 错误信息，不可为 null
	 * @param index   错误发生的位置索引（从 0 开始）
	 */
	public SyntaxException(@NotNull String message, int index) {
		this("SyntaxException: #" + index + ": " + message);
	}

	/**
	 * 使用错误信息、错误位置索引和原始文本构造一个语法异常。
	 * <p>
	 * 会生成包含错误位置指示符（^）的可视化文本。
	 * </p>
	 *
	 * @param message 错误信息，不可为 null
	 * @param index   错误发生的位置索引（从 0 开始）
	 * @param text    原始输入文本，不可为 null
	 */
	public SyntaxException(@NotNull String message, int index, @NotNull String text) {
		this(message + generationLocation(index, text), index);
	}

	/**
	 * 使用错误信息、错误位置索引、错误长度和原始文本构造一个语法异常。
	 * <p>
	 * 会生成包含多个 ^ 的可视化错误范围指示。
	 * </p>
	 *
	 * @param message 错误信息，不可为 null
	 * @param index   错误起始位置索引（从 0 开始）
	 * @param length  错误长度
	 * @param text    原始输入文本，不可为 null
	 */
	public SyntaxException(@NotNull String message, int index, int length, @NotNull String text) {
		this(message + generationLocation(index, length, text), index);
	}

	/**
	 * 根据指定索引生成单字符错误位置的可视化指示信息。
	 * <p>
	 * 示例输出：
	 * <pre>
	 * | abcdef
	 * | ~~^~~~
	 * </pre>
	 * </p>
	 *
	 * @param index 错误位置索引
	 * @param text  原始文本，不可为 null
	 * @return 包含可视化定位信息的字符串
	 */
	@NotNull
	private static String generationLocation(int index, @NotNull String text) {
		Objects.requireNonNull(text, "text is null");

		int safeIndex = Math.max(0, index);
		return "\n| " + text
			+ "\n| " + ("~".repeat(safeIndex)) + "^" + ("~".repeat(Math.max(0, text.length() - safeIndex - 1)));
	}

	/**
	 * 根据指定索引和长度生成错误范围的可视化指示信息。
	 * <p>
	 * 示例输出：
	 * <pre>
	 * | abcdef
	 * | ~~^^^~
	 * </pre>
	 * </p>
	 *
	 * @param index  错误起始位置索引
	 * @param length 错误长度
	 * @param text   原始文本，不可为 null
	 * @return 包含可视化定位信息的字符串
	 */
	@NotNull
	private static String generationLocation(int index, int length, @NotNull String text) {
		Objects.requireNonNull(text, "text is null");

		int safeIndex = Math.max(0, index);
		int safeLength = Math.max(0, length);
		return "\n| " + text
			+ "\n| " + ("~".repeat(safeIndex)) + ("^".repeat(safeLength)) + ("~".repeat(Math.max(0, text.length() - safeIndex - safeLength)));
	}
}
