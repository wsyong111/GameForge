package io.github.wsyong11.gameforge.framework.lang;

import io.github.wsyong11.gameforge.framework.lang.ast.node.ASTNode;
import io.github.wsyong11.gameforge.framework.lang.glsl.GlslTokenParser;
import io.github.wsyong11.gameforge.framework.lang.glsl.ast.ShaderASTNode;
import io.github.wsyong11.gameforge.framework.lang.glsl.token.GlslTokenizer;
import io.github.wsyong11.gameforge.framework.lang.token.Token;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
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
		GlslTokenizer tokenizer = new GlslTokenizer();

		Iterator<Token> tokenIterator = tokenizer.tokenize(SRC);
		while (tokenIterator.hasNext()) {
			System.out.println(tokenIterator.next());
		}
//		GlslTokenParser parser = new GlslTokenParser(tokenIterator);
//		ShaderASTNode ast = parser.parse();
//
//		print(ast, 0);
	}

	private static void print(@NotNull ASTNode node, int indent) {
		Objects.requireNonNull(node, "node is null");

		System.out.println("|" + "\t".repeat(indent) + node);

		for (ASTNode child : node.getChildren())
			print(child, indent + 1);
	}
}