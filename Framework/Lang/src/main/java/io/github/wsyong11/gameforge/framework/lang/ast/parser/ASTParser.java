package io.github.wsyong11.gameforge.framework.lang.ast.parser;

import io.github.wsyong11.gameforge.framework.lang.ast.node.ASTNode;
import io.github.wsyong11.gameforge.framework.lang.ast.parser.error.CompileReportCollector;
import io.github.wsyong11.gameforge.framework.lang.ast.parser.error.DefaultCompileReportCollector;
import io.github.wsyong11.gameforge.framework.lang.ast.parser.stream.IteratorTokenStream;
import io.github.wsyong11.gameforge.framework.lang.ast.parser.stream.TokenStream;
import io.github.wsyong11.gameforge.framework.lang.token.Token;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class ASTParser<T extends ASTNode> {
	private static final Logger LOGGER = Log.getLogger();

	private final List<ASTParserStrategy> strategies;

	protected ASTParser(@NotNull List<ASTParserStrategy> strategies) {
		Objects.requireNonNull(strategies, "strategies is null");
		this.strategies = strategies;
	}

	@NotNull
	protected abstract T createTree();

	@NotNull
	public T parse(@NotNull Iterator<Token> iterator) {
		Objects.requireNonNull(iterator, "iterator is null");

		T ast = this.createTree();

		TokenStream tokenStream = new IteratorTokenStream(iterator);
		CompileReportCollector reportCollector = new DefaultCompileReportCollector();

		while (tokenStream.hasNext()) {
			Token token = tokenStream.next();

		}

		return ast;
	}
}
