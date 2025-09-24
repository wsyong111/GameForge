package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Tokenizer {
	private final List<TokenRule> rules;

	public Tokenizer(@NotNull List<TokenRule> rules) {
		Objects.requireNonNull(rules, "rules is null");
		this.rules = List.copyOf(rules);
	}

	@NotNull
	@Unmodifiable
	public List<TokenRule> getRules() {
		return this.rules;
	}

	@NotNull
	public Iterator<Token> tokenize(@NotNull String src) {
		Objects.requireNonNull(src, "src is null");

		return new TokenizerIterator(this.rules, src);
	}

	@NotNull
	public Stream<Token> stream(@NotNull String src) {
		Objects.requireNonNull(src, "src is null");

		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
			this.tokenize(src),
			Spliterator.ORDERED
		), false);
	}

	protected static class TokenizerIterator implements Iterator<Token> {
		private final List<TokenRule> rules;
		private final String src;

		private final TokenContextImpl tokenContext;

		private int index;

		public TokenizerIterator(@NotNull List<TokenRule> rules, @NotNull String src) {
			Objects.requireNonNull(rules, "rules is null");
			Objects.requireNonNull(src, "src is null");

			this.rules = rules;
			this.src = src;

			this.tokenContext = new TokenContextImpl(src);

			this.index = 0;
		}

		private void skipWhitespace() {
			int codePoint;
			while (this.index < this.src.length() &&
				Character.isWhitespace(codePoint = this.src.codePointAt(this.index)))
				this.index += Character.charCount(codePoint);
		}

		@Override
		public boolean hasNext() {
			this.skipWhitespace();
			return this.index <= this.src.length();
		}

		@Override
		public Token next() {
			this.skipWhitespace();

			if (this.index == this.src.length()) {
				this.index++;
				return new EOFToken(this.src.length());
			}

			if (this.index > this.src.length())
				throw new NoSuchElementException();

			int codePoint = this.src.codePointAt(this.index);

			for (TokenRule rule : rules) {
				this.tokenContext.update(this.index);

				rule.tryMatch(this.tokenContext);
				if (!this.tokenContext.hasResult())
					continue;

				this.index += this.tokenContext.getLength();
				return this.tokenContext.getToken();
			}

			Token err = ErrorToken.ofCodePoint(codePoint, index);
			this.index += Character.charCount(codePoint);
			return err;
		}

		private static class TokenContextImpl implements TokenContext {
			private final String source;

			private final StringBuilder stringBuilder;

			private int index;

			private boolean haveResult;
			private Token token;
			private int length;

			private TokenContextImpl(@NotNull String source) {
				Objects.requireNonNull(source, "source is null");

				this.source = source;

				this.stringBuilder = new StringBuilder();

				this.index = 0;

				this.haveResult = false;
				this.token = null;
				this.length = 0;
			}

			public void update(int index) {
				this.stringBuilder.setLength(0);
				this.index = index;

				this.haveResult = false;
				this.token = null;
				this.length = 0;
			}

			public boolean hasResult() {
				return this.haveResult;
			}

			@NotNull
			public Token getToken() {
				if (!this.haveResult)
					throw new IllegalStateException("The result is empty");

				return this.token;
			}

			public int getLength() {
				if (!this.haveResult)
					throw new IllegalStateException("The result is empty");

				return this.length;
			}

			@NotNull
			@Override
			public String getSource() {
				return this.source;
			}

			@NotNull
			@Override
			public StringBuilder getStringBuilder() {
				return this.stringBuilder;
			}

			@Override
			public int getIndex() {
				return this.index;
			}

			@Override
			public void setResult(@NotNull Token token, int length) {
				Objects.requireNonNull(token, "token is null");

				this.haveResult = true;
				this.token = token;
				this.length = length;
			}
		}
	}

	public interface TokenRule {
		void tryMatch(@NotNull TokenContext context);
	}

	public interface TokenContext {
		@NotNull
		String getSource();

		@NotNull
		StringBuilder getStringBuilder();

		int getIndex();

		void setResult(@NotNull Token token, int length);
	}
}