package io.github.wsyong11.gameforge.framework.lang.token;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Tokenizer {
	private final Set<String> keywords;
	private final Set<String> operators;
	private final Set<Integer> stringToken;

	private final String commentToken;
	private final String multiLineCommentStartToken;
	private final String multiLineCommentEndToken;

	private final int operatorsMaxLength;

	public Tokenizer(
		@NotNull Set<String> keywords,
		@NotNull Set<String> operators,
		@NotNull String commentToken,
		@NotNull String multiLineCommentStartToken,
		@NotNull String multiLineCommentEndToken
	) {
		this(keywords, operators, Set.of((int) '"', (int) '\''), commentToken, multiLineCommentStartToken, multiLineCommentEndToken);
	}

	public Tokenizer(
		@NotNull Set<String> keywords,
		@NotNull Set<String> operators,
		@NotNull Set<Integer> stringToken,
		@NotNull String commentToken,
		@NotNull String multiLineCommentStartToken,
		@NotNull String multiLineCommentEndToken
	) {
		Objects.requireNonNull(keywords, "keywords is null");
		Objects.requireNonNull(operators, "operators is null");
		Objects.requireNonNull(stringToken, "stringToken is null");

		this.keywords = keywords;
		this.operators = operators;
		this.stringToken = stringToken;

		this.commentToken = commentToken;
		this.multiLineCommentStartToken = multiLineCommentStartToken;
		this.multiLineCommentEndToken = multiLineCommentEndToken;

		this.operatorsMaxLength = operators
			.stream()
			.mapToInt(String::length)
			.max()
			.orElse(0);
	}

	@NotNull
	public List<Token> tokenize(@NotNull String src) {
		Objects.requireNonNull(src, "src is null");

		List<Token> tokens = new ArrayList<>();

		StringBuilder sb = new StringBuilder();

		int length = src.length();

		int index = 0;
		while (index < length) {
			int c = src.codePointAt(index);

			if (Character.isWhitespace(c)) {
				index += Character.charCount(c);
				continue;
			}
			// 单行注释
			if (src.startsWith(this.commentToken, index)) {
				int beginIndex = index;
				index += this.commentToken.length();

				while (index < length) {
					int currentChar = src.codePointAt(index);
					if (currentChar == '\n' || currentChar == '\r')
						break;

					sb.appendCodePoint(currentChar);
					index += Character.charCount(currentChar);
				}

				tokens.add(new CommentToken(sb.toString(), beginIndex));
				sb.setLength(0);
				continue;
			}

			if (src.startsWith(this.multiLineCommentStartToken, index)) {
				int beginIndex = index;
				index += this.multiLineCommentStartToken.length();

				while (index < length) {
					if (src.startsWith(this.multiLineCommentEndToken, index)) {
						index += this.multiLineCommentEndToken.length();
						break;
					}

					int currentChar = src.codePointAt(index);
					sb.appendCodePoint(currentChar);
					index += Character.charCount(currentChar);
				}

				tokens.add(new MultiLineCommentToken(sb.toString(), beginIndex));
				sb.setLength(0);
				continue;
			}


			if (this.stringToken.contains(c)) {
				int beginIndex = index;
				index += Character.charCount(c);

				boolean esc = false;
				while (index < length) {
					int currentChar = src.codePointAt(index);

					if (!esc && currentChar == '\\') {
						esc = true;
						index += Character.charCount(currentChar);
						continue;
					}

					if (!esc && currentChar == c)
						break;

					esc = false;

					sb.appendCodePoint(currentChar);
					index += Character.charCount(currentChar);
				}

				index += Character.charCount(c);

				tokens.add(new StringToken(sb.toString(), beginIndex));
				sb.setLength(0);
				continue;
			}

			String op = this.tryMatchOperator(src, index);
			if (op != null) {
				tokens.add(new OperatorToken(op, index));
				index += op.length();
				continue;
			}

			if (Character.isDigit(c)) {
				int beginIndex = index;

				while (index < length) {
					int currentChar = src.codePointAt(index);

					if (!Character.isDigit(currentChar) && src.codePointAt(index) != '.')
						break;

					sb.appendCodePoint(currentChar);
					index += Character.charCount(currentChar);
				}

				tokens.add(new NumberToken(sb.toString(), beginIndex));
				sb.setLength(0);
				continue;
			}

			if (Character.isLetter(c) || c == '_') {
				int beginIndex = index;

				while (index < length) {
					int currentChar = src.codePointAt(index);

					if (!Character.isLetterOrDigit(currentChar) && currentChar != '_')
						break;

					sb.appendCodePoint(currentChar);
					index += Character.charCount(currentChar);
				}

				String word = sb.toString();
				sb.setLength(0);

				Token token = this.keywords.contains(word)
					? new KeywordToken(word, beginIndex)
					: new IdentToken(word, beginIndex);

				tokens.add(token);

				continue;
			}

			tokens.add(new ErrorToken(new String(Character.toChars(c)), index));
			index += Character.charCount(c);
		}

		return tokens;
	}

	@Nullable
	private String tryMatchOperator(@NotNull String src, int i) {
		Objects.requireNonNull(src, "src is null");

		if (this.operatorsMaxLength == 0)
			return null;

		for (int len = this.operatorsMaxLength; len > 0; len--) {
			if (i + len <= src.length()) {
				String sub = src.substring(i, i + len);
				if (this.operators.contains(sub))
					return sub;
			}
		}
		return null;
	}
}

