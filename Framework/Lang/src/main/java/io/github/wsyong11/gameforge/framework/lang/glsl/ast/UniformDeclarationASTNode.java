package io.github.wsyong11.gameforge.framework.lang.glsl.ast;

import io.github.wsyong11.gameforge.framework.lang.ast.node.ASTNode;
import io.github.wsyong11.gameforge.framework.lang.ast.node.AbstractASTNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UniformDeclarationASTNode extends AbstractASTNode<UniformDeclarationASTNode> {
	protected UniformDeclarationASTNode(@NotNull List<ASTNode> defaultChildren) {
		super(defaultChildren);
	}

	public UniformDeclarationASTNode() {
	}

	@NotNull
	@Override
	protected UniformDeclarationASTNode copySelf(@NotNull List<ASTNode> clonedChildren) {
		return new UniformDeclarationASTNode(clonedChildren);
	}

	@Override
	public String toString() {
		return "UniformDeclaration()";
	}
}
