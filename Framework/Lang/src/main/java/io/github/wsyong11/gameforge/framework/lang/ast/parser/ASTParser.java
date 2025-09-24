package io.github.wsyong11.gameforge.framework.lang.ast.parser;

import io.github.wsyong11.gameforge.framework.lang.ast.node.ASTNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public abstract class ASTParser<T extends ASTNode> {
	private final List<ASTParserStrategy> strategies;

	protected ASTParser(@NotNull List<ASTParserStrategy> strategies) {
		Objects.requireNonNull(strategies, "strategies is null");
		this.strategies = strategies;
	}

	@NotNull
	protected abstract T createTree();

	@NotNull
	public T parse() {
		T ast = this.createTree();



		return ast;
	}

}
