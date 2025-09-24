package io.github.wsyong11.gameforge.framework.lang.ast.node;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ASTNodeVisitor {
	void visit(@NotNull ASTNode node);
}
