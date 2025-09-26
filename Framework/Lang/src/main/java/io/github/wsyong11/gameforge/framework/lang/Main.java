package io.github.wsyong11.gameforge.framework.lang;

import io.github.wsyong11.gameforge.framework.lang.ast.node.ASTNode;
import io.github.wsyong11.gameforge.framework.lang.glsl.ast.GlslAST;
import io.github.wsyong11.gameforge.framework.lang.glsl.ast.ShaderASTNode;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Main {
	@Language("glsl")
	private static final String SRC = """
		// Define uniform
		uniform vec4 color;

		/**
		 * Main
		 */
		void main() {
			// Set frag color to uniform value
			gl_FragColor = vec4(color, 1);
		}""";

	public static void main(String[] args) {
		ShaderASTNode ast = GlslAST.ast(
			GlslAST.declareUniform("vec4", "color")
		);

		print(ast, 0);
	}

	private static void print(@NotNull ASTNode node, int indent) {
		Objects.requireNonNull(node, "node is null");

		System.out.println("|" + "\t".repeat(indent) + node);

		for (ASTNode child : node.getChildren())
			print(child, indent + 1);
	}
}