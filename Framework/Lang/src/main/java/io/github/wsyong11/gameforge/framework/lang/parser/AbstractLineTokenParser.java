package io.github.wsyong11.gameforge.framework.lang.parser;

import io.github.wsyong11.gameforge.framework.lang.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.lang.token.*;
import io.github.wsyong11.gameforge.util.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractLineTokenParser<T> {
	private final String text;
	private final TokenIterator iterator;

	@Nullable
	private volatile T result;

	protected AbstractLineTokenParser(@NotNull Tokenizer tokenizer, @NotNull String text) {
		Objects.requireNonNull(tokenizer, "tokenizer is null");
		Objects.requireNonNull(text, "text is null");

		this.text = text;
		this.iterator = new TokenIterator(tokenizer.tokenize(text));

		this.result = null;
	}

	protected AbstractLineTokenParser(@NotNull String text, @NotNull TokenIterator iterator) {
		Objects.requireNonNull(text, "text is null");
		Objects.requireNonNull(iterator, "iterator is null");

		this.text = text;
		this.iterator = iterator;

		this.result = null;
	}

	protected AbstractLineTokenParser(@NotNull String text, @NotNull Iterator<Token> iterator) {
		Objects.requireNonNull(text, "text is null");
		Objects.requireNonNull(iterator, "iterator is null");

		this.text = text;
		this.iterator = new TokenIterator(iterator);

		this.result = null;
	}

	@NotNull
	public synchronized T parse() {
		T result = this.result;
		if (result != null)
			return result;

		T parseResult = this.parse(this.iterator);
		this.result = parseResult;
		return parseResult;
	}

	@NotNull
	public String getText() {
		return this.text;
	}

	@NotNull
	protected abstract T parse(@NotNull TokenIterator iterator);

	// -------------------------------------------------------------------------------------------------------------- //

	protected boolean hasNext() {
		return this.iterator.hasNext();
	}

	@NotNull
	protected Token next() {
		return this.checkToken(this.iterator.next());
	}

	@NotNull
	protected Token peek() {
		return this.iterator.peek();
	}

	protected void rewind() {
		this.iterator.rewind();
	}

	protected void rewind(int count) {
		for (int i = 0; i < count; i++)
			this.iterator.rewind();
	}

	@NotNull
	protected Token getCurrent() {
		return this.iterator.getCurrent();
	}

	@NotNull
	protected String getCurrentToken() {
		return this.getCurrent().getToken();
	}

	@NotNull
	protected TokenIterator getIterator() {
		return this.iterator;
	}

	@Contract("_ -> param1")
	@NotNull
	protected Token checkToken(@NotNull Token token) {
		Objects.requireNonNull(token, "token is null");

		if (token instanceof EOFToken)
			throw this.unexpectedEOFError();

		if (token instanceof ErrorToken)
			throw this.invalidTokenError(token);

		return token;
	}

	protected boolean matchCurrent(@NotNull String value) {
		Objects.requireNonNull(value, "value is null");
		return this.getCurrent().equalsToken(value);
	}

	protected boolean matchCurrent(@NotNull Class<? extends Token> type) {
		Objects.requireNonNull(type, "type is null");
		return this.getCurrent().equalsToken(type);
	}

	protected boolean matchCurrent(@NotNull Class<? extends Token> type, @NotNull String value) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(value, "value is null");
		return this.getCurrent().equalsToken(type, value);
	}

	protected boolean consumeIfMatch(@NotNull String value) {
		Objects.requireNonNull(value, "value is null");

		Token token = this.getCurrent();
		if (token.equalsToken(value)) {
			this.next();
			return true;
		}
		return false;
	}

	protected boolean consumeIfMatch(@NotNull Class<? extends Token> type) {
		Objects.requireNonNull(type, "type is null");

		Token token = this.getCurrent();
		if (token.equalsToken(type)) {
			this.next();
			return true;
		}
		return false;
	}

	protected boolean consumeIfMatch(@NotNull Class<? extends Token> type, @NotNull String value) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(value, "value is null");

		Token token = this.getCurrent();
		if (token.equalsToken(type, value)) {
			this.next();
			return true;
		}
		return false;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	protected SyntaxException unexpectedEOFError() {
		return new SyntaxException(
			"Unexpected end of input",
			this.text,
			this.text.length(),
			1
		);
	}

	@NotNull
	protected SyntaxException invalidTokenError() {
		return this.invalidTokenError(this.getCurrent());
	}

	@NotNull
	protected SyntaxException invalidTokenError(@NotNull Token token) {
		Objects.requireNonNull(token, "token is null");
		return this.syntaxError(token, "Invalid syntax");
	}

	@NotNull
	protected SyntaxException expectedTokenError(@NotNull String expected) {
		return this.expectedTokenError(this.getCurrent(), expected);
	}

	@NotNull
	protected SyntaxException expectedTokenError(@NotNull Token token, @NotNull String expected) {
		Objects.requireNonNull(token, "token is null");
		Objects.requireNonNull(expected, "expected is null");
		return this.syntaxError(token, "Expected '%s' but got '%s'", expected, token.getToken());
	}

	@NotNull
	protected SyntaxException expectedTokenError(@NotNull String... texts) {
		return this.expectedTokenError(this.getCurrent(), texts);
	}

	@NotNull
	protected SyntaxException expectedTokenError(@NotNull Token token, @NotNull String... texts) {
		Objects.requireNonNull(token, "token is null");
		Objects.requireNonNull(texts, "texts is null");

		// 'a', 'b' or 'c'
		String expectedText = Arrays
			.stream(texts)
			.map(StringUtils::wrapWithQuotes)
			.collect(Collectors.collectingAndThen(
				Collectors.toList(),
				list -> {
					int size = list.size();
					if (size == 1)
						return list.get(0);

					return String.join(", ", list.subList(0, size - 1))
						+ " or " + list.get(size - 1);
				}
			));

		String tokenText = StringUtils.wrapWithQuotes(token.getToken());

		return this.syntaxError(token, "Expected %s, but %s found", expectedText, tokenText);
	}

	@NotNull
	protected SyntaxException unexpectedTokenError() {
		return this.unexpectedTokenError(this.getCurrent());
	}

	@NotNull
	protected SyntaxException unexpectedTokenError(@NotNull Token token) {
		Objects.requireNonNull(token, "token is null");
		return this.syntaxError(token, "Unexpected token '%s'", token.getToken());
	}

	@NotNull
	protected SyntaxException missingTokenError(@NotNull String tokenName, int index) {
		Objects.requireNonNull(tokenName, "tokenName is null");
		return new SyntaxException("Missing required token '%s'".formatted(tokenName), this.text, index, 1);
	}

	@NotNull
	protected SyntaxException redundantTokenError() {
		return this.redundantTokenError(this.getCurrent());
	}

	@NotNull
	protected SyntaxException redundantTokenError(@NotNull Token token) {
		Objects.requireNonNull(token, "token is null");
		return this.syntaxError(token, "Redundant token '%s' found", token.getToken());
	}

	@NotNull
	protected SyntaxException invalidNumberError() {
		return this.invalidNumberError(this.getCurrent());
	}

	@NotNull
	protected SyntaxException invalidNumberError(@NotNull Token token) {
		Objects.requireNonNull(token, "token is null");
		return this.syntaxError(token, "Invalid number '%s'", token.getToken());
	}

	@NotNull
	protected SyntaxException invalidIdentifierError() {
		return this.invalidIdentifierError(this.getCurrent());
	}

	@NotNull
	protected SyntaxException invalidIdentifierError(@NotNull Token token) {
		Objects.requireNonNull(token, "token is null");
		return this.syntaxError(token, "Invalid identifier '%s'", token.getToken());
	}

	@NotNull
	protected SyntaxException unmatchedBracketError() {
		return this.unmatchedBracketError(this.getCurrent());
	}

	@NotNull
	protected SyntaxException unmatchedBracketError(@NotNull Token token) {
		Objects.requireNonNull(token, "token is null");
		return this.syntaxError(token, "Unmatched bracket '%s'", token.getToken());
	}

	@NotNull
	protected SyntaxException valueOutOfRangeError(int min, int max) {
		return this.valueOutOfRangeError(this.getCurrent(), min, max);
	}

	@NotNull
	protected SyntaxException valueOutOfRangeError(@NotNull Token token, int min, int max) {
		Objects.requireNonNull(token, "token is null");
		return this.syntaxError(token, "Value '%s' out of range [%d, %d]", token.getToken(), min, max);
	}

	@NotNull
	protected SyntaxException syntaxError(@NotNull String message, Object... formatArgs) {
		Objects.requireNonNull(message, "message is null");
		Objects.requireNonNull(formatArgs, "formatArgs is null");
		return this.syntaxError(this.getCurrent(), message, formatArgs);
	}

	@NotNull
	protected SyntaxException syntaxError(@NotNull Token token, @NotNull String message, Object... formatArgs) {
		Objects.requireNonNull(token, "token is null");
		Objects.requireNonNull(message, "message is null");
		Objects.requireNonNull(formatArgs, "formatArgs is null");

		return new SyntaxException(
			message.formatted(formatArgs),
			this.text,
			token.getSrcIndex(),
			Math.max(1, token.length())
		);
	}
}
