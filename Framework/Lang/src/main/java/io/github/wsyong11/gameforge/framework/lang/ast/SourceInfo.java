package io.github.wsyong11.gameforge.framework.lang.ast;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public interface SourceInfo {
	/**
	 * 获取开始位置
	 *
	 * @return 开始位置的字符绝对索引
	 */
	int getStartIndex();

	/**
	 * 获取结束位置
	 *
	 * @return 结束位置的字符绝对索引
	 */
	int getEndIndex();

	/**
	 * 获取源代码所在的行号
	 *
	 * @return 源代码所在的绝对行号，从1开始，第一行源代码
	 */
	int getRow();

	/**
	 * 获取源代码所在的列号
	 *
	 * @return 源代码所在的绝对列号，从1开始
	 */
	int getCol();

	/**
	 * 获取源代码片段
	 *
	 * @return 源代码片段（至少包含 startIndex 到 endIndex）
	 */
	@NotNull
	String getSource();

	/**
	 * 获取源代码文件
	 *
	 * @return 源代码文件 URL 对象
	 */
	@NotNull
	URL getFile();

	/**
	 * 获取源代码文件名
	 *
	 * @return 源代码文件名，可能是任意内容
	 */
	@NotNull
	String getFileName();
}
