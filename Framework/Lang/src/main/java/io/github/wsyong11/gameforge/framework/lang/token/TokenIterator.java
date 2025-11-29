package io.github.wsyong11.gameforge.framework.lang.token;

import io.github.wsyong11.gameforge.util.collection.RewindableIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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

	@NotNull
	public TokenIterator slice(int length) {
		int cursor = this.getCursor();
		return new SliceTokenIterator(this, cursor, cursor + length);
	}

	private static class SliceTokenIterator extends TokenIterator {
		private final TokenIterator parent;
		private final int startCursor;
		private final int endCursor;

		public SliceTokenIterator(TokenIterator parent, int startCursor, int endCursor) {
			super(Collections.emptyIterator());
			this.parent = parent;
			this.startCursor = startCursor;
			this.endCursor = endCursor;
		}


	}
}
