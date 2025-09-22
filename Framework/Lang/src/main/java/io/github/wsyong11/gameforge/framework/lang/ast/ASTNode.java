package io.github.wsyong11.gameforge.framework.lang.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface ASTNode {
	@Nullable
	ASTNode getParent();

	@NotNull
	@UnmodifiableView
	List<ASTNode> getChildren();

	@NotNull
	SourceInfo getSource();
}
