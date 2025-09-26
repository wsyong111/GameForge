package io.github.wsyong11.gameforge.framework.lang.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Objects;

public class SimpleSourceInfo implements SourceInfo {
	private final int startIndex;
	private final int endIndex;
	private final int row;
	private final int col;
	private final String source;
	@Nullable
	private final URL file;
	private final String fileName;

	public SimpleSourceInfo(
		int startIndex,
		int endIndex,
		int row,
		int col,
		@NotNull String source,
		@Nullable URL file,
		@NotNull String fileName
	) {
		Objects.requireNonNull(source, "source is null");
		Objects.requireNonNull(fileName, "fileName is null");

		if (startIndex < 0)
			throw new IllegalArgumentException("Start index cannot be negative");
		if (endIndex < 0)
			throw new IllegalArgumentException("End index cannot be negative");

		if (row < 1)
			throw new IllegalArgumentException("Row cannot be less than 1");
		if (col < 1)
			throw new IllegalArgumentException("Col cannot be less than 1");

		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.row = row;
		this.col = col;
		this.source = source;
		this.file = file;
		this.fileName = fileName;
	}

	@Override
	public int getStartIndex() {
		return this.startIndex;
	}

	@Override
	public int getEndIndex() {
		return this.endIndex;
	}

	@Override
	public int getRow() {
		return this.row;
	}

	@Override
	public int getCol() {
		return this.col;
	}

	@NotNull
	@Override
	public String getSource() {
		return this.source;
	}

	@Nullable
	@Override
	public URL getFile() {
		return this.file;
	}

	@NotNull
	@Override
	public String getFileName() {
		return this.fileName;
	}
}
