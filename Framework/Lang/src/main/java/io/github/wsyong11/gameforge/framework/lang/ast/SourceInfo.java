package io.github.wsyong11.gameforge.framework.lang.ast;

import org.jetbrains.annotations.NotNull;

public interface SourceInfo {
	int getStart();

	int getEnd();

	int getRowIndex();

	int getColIndex();

	@NotNull
	String getSource();
}
