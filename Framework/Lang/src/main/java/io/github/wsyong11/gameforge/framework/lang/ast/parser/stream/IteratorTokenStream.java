package io.github.wsyong11.gameforge.framework.lang.ast.parser.stream;

import io.github.wsyong11.gameforge.framework.lang.token.EOFToken;
import io.github.wsyong11.gameforge.framework.lang.token.IdentToken;
import io.github.wsyong11.gameforge.framework.lang.token.Token;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class IteratorTokenStream implements TokenStream {
	private final Iterator<Token> iterator;

	private final List<Token> tokens;

	private int index;

	public IteratorTokenStream(@NotNull Iterator<Token> iterator) {
		Objects.requireNonNull(iterator, "iterator is null");
		this.iterator = iterator;

		this.tokens = new ArrayList<>();

		this.index = 0;
	}

	/**
	 * 确保指定位置的 token 已经被加载，如果 iterator 用完了就追加 EOFToken
	 */
	private int ensureLoaded(int targetIndex) {
		while (this.tokens.size() <= targetIndex) {
			if (this.iterator.hasNext()) {
				this.tokens.add(this.iterator.next());
				continue;
			}

			// iterator 已经没有新 token 了，就补一个 EOF
			int size = this.tokens.size();
			if (size == 0 || !(this.tokens.get(size - 1) instanceof EOFToken)) {
				Token lastToken = this.tokens.get(size - 1);
				this.tokens.add(new EOFToken(lastToken.getSrcIndex() + lastToken.length()));
			}

			break;
		}
		return Math.min(targetIndex, this.tokens.size() - 1);
	}

	@Override
	public boolean hasNext() {
		return !(this.peek() instanceof EOFToken);
	}

	@NotNull
	@Override
	public Token next() {
		this.index = this.ensureLoaded(this.index);
		Token token = this.tokens.get(this.index);
		this.index++;
		return token;
	}

	@NotNull
	@Override
	public Token peek() {
		return this.peek(0);
	}

	@NotNull
	@Override
	public Token peek(int offset) {
		int index = this.ensureLoaded(this.index + offset);
		return this.tokens.get(index);
	}

	@Override
	public int position() {
		return this.index;
	}

	@Override
	public void rewind(int pos) {
		if (pos < 0)
			throw new IndexOutOfBoundsException("Invalid rewind position: " + pos);

		this.index = this.ensureLoaded(pos);
	}

	@Override
	public boolean match(@NotNull Class<? extends Token> type) {
		Objects.requireNonNull(type, "type is null");

		Token token = this.peek();
		if (!type.isInstance(token))
			return false;

		this.next();
		return true;
	}

	@Override
	public boolean match(@NotNull String literal) {
		Objects.requireNonNull(literal, "literal is null");

		Token token = this.peek();
		if (!(token instanceof IdentToken) && !literal.equals(token.getToken()))
			return false;

		this.next();
		return true;
	}

//	@NotNull
//	@Override
//	public Token expect(@NotNull Class<? extends Token> type, @NotNull CompileReportCollector errors) {
//		return null;
//	}
//
//	@NotNull
//	@Override
//	public Token expect(String literal, @NotNull CompileReportCollector errors) {
//		return null;
//	}

	@Override
	public void syncUntil(@NotNull Predicate<Token> predicate) {
		Objects.requireNonNull(predicate, "predicate is null");

		while (this.hasNext()) {
			Token token = this.next();
			if (!predicate.test(token))
				break;
		}
	}
}
