package io.github.wsyong11.gameforge.framework.lang.glsl.ast;

import io.github.wsyong11.gameforge.framework.lang.ast.node.ASTNode;
import io.github.wsyong11.gameforge.framework.lang.ast.node.AbstractASTNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShaderASTNode extends AbstractASTNode<ShaderASTNode> {
	protected ShaderASTNode(@NotNull List<ASTNode> defaultChildren) {
		super(defaultChildren);
	}

	public ShaderASTNode() {
	}

	@NotNull
	@Override
	protected ShaderASTNode copySelf(@NotNull List<ASTNode> clonedChildren) {
		return new ShaderASTNode(clonedChildren);
	}

	@Override
	public String toString() {
		return "Shader()";
	}
}
