package io.github.wsyong11.gameforge.framework.lang.ast.node;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IdentifierNode extends AbstractASTNode<IdentifierNode> {
	private final String identifier;

	public IdentifierNode(@NotNull String identifier) {
		this.identifier = identifier;
	}

	@NotNull
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	protected boolean hasChildren() {
		return false;
	}

	@NotNull
	@Override
	protected IdentifierNode copySelf(@NotNull List<ASTNode> clonedChildren) {
		return new IdentifierNode(this.identifier);
	}

	@Override
	public String toString() {
		return "Identifier(\"" + this.identifier + "\")";
	}
}
