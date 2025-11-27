package io.github.wsyong11.gameforge.framework.lang.token;

import io.github.wsyong11.gameforge.util.collection.RewindableIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class TokenIterator extends RewindableIterator<Token> implements Iterator<Token> {
	private Token current;
	private Token peeked;

	public TokenIterator(@NotNull Iterator<Token> source) {
		super(source);
		this.current = null;
		this.peeked = null;
	}

	@Override
	public Token next() {
		if (!super.hasNext()) {
			this.current = new EOFToken(this.current != null ? this.current.getSrcIndex() : 0);
			this.peeked = null;
			return this.current;
		}

		Token token = super.next();
		this.current = token;
		this.peeked = null;
		return token;
	}

	@NotNull
	public Token peek() {
		if (this.peeked != null)
			return this.peeked;

		if (this.isRewinding())
			return peeked = super.getHistory(-1);

		if (super.hasNext()) {
			this.peeked = super.next();
			this.rewind();
			return this.peeked;
		}

		return this.peeked = new EOFToken(this.current != null ? this.current.getSrcIndex() : 0);
	}

	@Override
	public void rewind() {
		super.rewind();
		this.peeked = null;
		this.current = (this.getCursor() > 0) ? this.getHistory(0) : null;
	}

	@NotNull
	public Token getCurrent() {
		if (this.current == null)
			this.next();

		return this.current;
	}
}
