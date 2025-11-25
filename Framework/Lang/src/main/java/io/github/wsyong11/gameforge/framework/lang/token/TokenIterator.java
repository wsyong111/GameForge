package io.github.wsyong11.gameforge.framework.lang.token;

import io.github.wsyong11.gameforge.util.collection.RewindableIterator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;

public class TokenIterator extends RewindableIterator<Token> implements Iterator<Token> {
	private final Deque<Token> tokenStack;
	private Token current;
	private Token peeked;

	public TokenIterator(@NotNull Iterator<Token> source) {
		super(source);
		this.tokenStack = new ArrayDeque<>();
		this.current = null;
		this.peeked = null;
	}

	@Override
	public boolean hasNext() {
		return !this.tokenStack.isEmpty() || super.hasNext();
	}

	@Override
	public Token next() {
		if (this.peeked != null) {
			this.current = this.peeked;
			this.peeked = null;
			return this.current;
		}

		if (!this.tokenStack.isEmpty())
			return this.current = this.tokenStack.pop();

		if (!super.hasNext())
			return this.current = new EOFToken(this.current != null ? this.current.getSrcIndex() : 0);

		return this.current = super.next();
	}

	public void putToken(@NotNull Token token) {
		Objects.requireNonNull(token, "token is null");
		this.tokenStack.push(token);
	}

	@NotNull
	public Token peek() {
		if (this.peeked == null) {
			if (!this.tokenStack.isEmpty())
				this.peeked = this.tokenStack.peek();
			else if (super.hasNext())
				this.peeked = super.next();
			else
				this.peeked = new EOFToken(this.current != null ? this.current.getSrcIndex() : 0);
		}
		return this.peeked;
	}

	@NotNull
	public Token getCurrent() {
		if (this.current == null)
			this.next();

		return this.current;
	}
}
