package io.github.wsyong11.gameforge.framework.lang.ast;

import io.github.wsyong11.gameforge.framework.lang.ast.node.ASTNode;
import io.github.wsyong11.gameforge.framework.lang.token.Token;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

public abstract class AbstractASTParser<T extends ASTNode> {
	protected static final Token EOF_TOKEN = EOFToken.INSTANCE;

	private final Iterator<Token> iterator;

	private Token currentToken;
	private int tokenIndex;

	private volatile T ast;
	private volatile boolean complete;

	public AbstractASTParser(@NotNull Iterator<Token> iterator) {
		Objects.requireNonNull(iterator, "iterator is null");

		this.iterator = iterator;

		this.currentToken = null;
		this.tokenIndex = -1;

		this.complete = false;

		this.next();
	}

	@NotNull
	protected abstract T createAST();

	protected abstract void parseTopLevel(@NotNull T ast);

	@NotNull
	public synchronized T parse() {
		if (this.complete)
			return this.ast;

		this.ast = this.createAST();
		while (this.hasNext())
			this.parseTopLevel(this.ast);

		this.complete = true;
		return this.ast;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected boolean hasNext() {
		return this.iterator.hasNext();
	}

	@NotNull
	protected Token next() {
		if (this.iterator.hasNext()) {
			this.currentToken = this.iterator.next();
			this.tokenIndex++;
		} else {
			this.currentToken = EOFToken.INSTANCE;
		}

		return this.currentToken;
	}

	@NotNull
	protected Token peek() {
		return this.currentToken;
	}

	protected int currentTokenIndex() {
		return this.tokenIndex;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected static class EOFToken implements Token {
		public static final EOFToken INSTANCE = new EOFToken();

		private EOFToken() { /* no-op */ }

		@NotNull
		@Override
		public String getToken() {
			return "";
		}

		@Override
		public int length() {
			return 0;
		}

		@Override
		public int getSrcIndex() {
			return 0;
		}

		@NotNull
		@Override
		public String toString() {
			return "EOF";
		}
	}
}
