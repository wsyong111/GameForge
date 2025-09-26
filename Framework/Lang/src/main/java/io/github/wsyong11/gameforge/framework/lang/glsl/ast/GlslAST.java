package io.github.wsyong11.gameforge.framework.lang.glsl.ast;

import io.github.wsyong11.gameforge.framework.lang.ast.node.ASTNode;
import io.github.wsyong11.gameforge.framework.lang.ast.node.IdentifierNode;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@UtilityClass
public class GlslAST {
	@NotNull
	public static ShaderASTNode ast(@NotNull ASTNode... childs) {
		Objects.requireNonNull(childs, "childs is null");
		return new ShaderASTNode(List.of(childs));
	}

	@NotNull
	public static IdentifierNode ident(@NotNull String ident) {
		Objects.requireNonNull(ident, "ident is null");
		return new IdentifierNode(ident);
	}

	@NotNull
	public static UniformDeclarationASTNode declareUniform(@NotNull String type, @NotNull String name) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(name, "name is null");

		return new UniformDeclarationASTNode(List.of(
			ident(type),
			ident(name)
		));
	}
}
