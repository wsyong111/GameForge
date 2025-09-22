package io.github.wsyong11.gameforge.framework.lang.token;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

@UtilityClass
public class TokenRules {
	@NotNull
	public static Tokenizer.TokenRule ofKeywords(@NotNull Set<String> keywords) {
		Objects.requireNonNull(keywords, "keywords is null");
		return new KeywordRule(keywords);
	}

	@NotNull
	public static Tokenizer.TokenRule ofNumber() {
		return new NumberRule();
	}

	@NotNull
	public static Tokenizer.TokenRule ofOperator(@NotNull Set<String> operators) {
		Objects.requireNonNull(operators, "operators is null");
		return new OperatorRule(operators);
	}

	@NotNull
	public static Tokenizer.TokenRule ofString(@NotNull Set<Integer> stringTokens) {
		Objects.requireNonNull(stringTokens, "stringTokens is null");
		return new StringRule(stringTokens);
	}

	@NotNull
	public static Tokenizer.TokenRule ofComment(@NotNull String commentToken) {
		Objects.requireNonNull(commentToken, "commentToken is null");
		return new CommentRule(commentToken);
	}

	@NotNull
	public static Tokenizer.TokenRule ofMultiLineComment(@NotNull String startToken, @NotNull String endToken) {
		Objects.requireNonNull(startToken, "startToken is null");
		Objects.requireNonNull(endToken, "endToken is null");
		return new MultiLineComment(startToken, endToken);
	}

	public static class KeywordRule implements Tokenizer.TokenRule {
		private final Set<String> keywords;

		public KeywordRule(@NotNull Set<String> keywords) {
			Objects.requireNonNull(keywords, "keywords is null");
			this.keywords = Set.copyOf(keywords);
		}

		@Override
		public void tryMatch(@NotNull Tokenizer.TokenContext context) {
			Objects.requireNonNull(context, "context is null");

			String src = context.getSource();
			StringBuilder sb = context.getStringBuilder();

			int index = context.getIndex();

			int c = src.codePointAt(index);
			if (!Character.isLetter(c) && c != '_')
				return;

			int i = index;
			while (i < src.length()) {
				int ch = src.codePointAt(i);
				if (!Character.isLetterOrDigit(ch) && ch != '_')
					break;

				sb.appendCodePoint(ch);
				i += Character.charCount(ch);
			}

			String word = sb.toString();
			Token token = this.keywords.contains(word)
				? new KeywordToken(word, index)
				: new IdentToken(word, index);

			context.setResult(token, i - index);
		}
	}

	public static class NumberRule implements Tokenizer.TokenRule {
		@Override
		public void tryMatch(@NotNull Tokenizer.TokenContext context) {
			Objects.requireNonNull(context, "context is null");

			int index = context.getIndex();
			String src = context.getSource();
			StringBuilder sb = context.getStringBuilder();

			boolean hasDigit = false;
			boolean hasDot = false;

			int i = index;
			while (i < src.length()) {
				int ch = src.codePointAt(i);

				if (Character.isDigit(ch)) {
					sb.appendCodePoint(ch);
					i += Character.charCount(ch);
					hasDigit = true;
					continue;
				}

				if (ch == '.' && !hasDot) {
					sb.appendCodePoint(ch);
					i += Character.charCount(ch);
					hasDot = true;
					continue;
				}

				break;
			}

			if (!hasDigit)
				return;

			context.setResult(new NumberToken(sb.toString(), index), i - index);
		}
	}

	public static class OperatorRule implements Tokenizer.TokenRule {
		private final Set<String> operators;
		private final int maxLen;

		public OperatorRule(@NotNull Set<String> operators) {
			Objects.requireNonNull(operators, "operators is null");

			this.operators = Set.copyOf(operators);
			this.maxLen = operators
				.stream()
				.mapToInt(String::length)
				.max()
				.orElse(0);
		}

		@Override
		public void tryMatch(@NotNull Tokenizer.TokenContext context) {
			Objects.requireNonNull(context, "context is null");

			int index = context.getIndex();
			String src = context.getSource();

			for (int len = this.maxLen; len > 0; len--) {
				if (index + len <= src.length()) {
					String sub = src.substring(index, index + len);
					if (!this.operators.contains(sub))
						continue;

					context.setResult(new OperatorToken(sub, index), len);
					return;
				}
			}
		}
	}

	public static class StringRule implements Tokenizer.TokenRule {
		private final Set<Integer> stringTokens;

		public StringRule(@NotNull Set<Integer> stringTokens) {
			Objects.requireNonNull(stringTokens, "stringTokens is null");
			this.stringTokens = Set.copyOf(stringTokens);
		}

		@Override
		public void tryMatch(@NotNull Tokenizer.TokenContext context) {
			Objects.requireNonNull(context, "context is null");

			int index = context.getIndex();
			String src = context.getSource();
			StringBuilder sb = context.getStringBuilder();

			int c = src.codePointAt(index);
			if (!this.stringTokens.contains(c))
				return;

			boolean esc = false;

			int i = index + Character.charCount(c);
			while (i < src.length()) {
				int ch = src.codePointAt(i);
				if (!esc && ch == '\\') {
					esc = true;
					i += Character.charCount(ch);
					continue;
				}

				if (!esc && ch == c) {
					context.setResult(
						new StringToken(sb.toString(), index),
						i - index
					);
					return;
				}

				esc = false;
				sb.appendCodePoint(ch);
				i += Character.charCount(ch);
			}

			context.setResult(
				ErrorToken.ofString(src.substring(index), index),
				src.length() - index
			);
		}
	}

	public static class CommentRule implements Tokenizer.TokenRule {
		private final String commentToken;

		public CommentRule(@NotNull String commentToken) {
			Objects.requireNonNull(commentToken, "commentToken is null");
			this.commentToken = commentToken;
		}

		@Override
		public void tryMatch(@NotNull Tokenizer.TokenContext context) {
			Objects.requireNonNull(context, "context is null");

			int index = context.getIndex();
			String src = context.getSource();
			StringBuilder sb = context.getStringBuilder();

			if (!src.startsWith(this.commentToken, index))
				return;

			int i = index + this.commentToken.length();

			while (i < src.length()) {
				int ch = src.codePointAt(i);
				if (ch == '\n' || ch == '\r')
					break;

				sb.appendCodePoint(ch);
				i += Character.charCount(ch);
			}

			context.setResult(new CommentToken(sb.toString(), index), i - index);
		}
	}

	public static class MultiLineComment implements Tokenizer.TokenRule {
		private final String startToken;
		private final String endToken;

		public MultiLineComment(@NotNull String startToken, @NotNull String endToken) {
			Objects.requireNonNull(startToken, "startToken is null");
			Objects.requireNonNull(endToken, "endToken is null");

			this.startToken = startToken;
			this.endToken = endToken;
		}

		@Override
		public void tryMatch(@NotNull Tokenizer.TokenContext context) {
			Objects.requireNonNull(context, "context is null");

			int index = context.getIndex();
			String src = context.getSource();
			StringBuilder sb = context.getStringBuilder();

			if (!src.startsWith(this.startToken, index))
				return;

			int i = index + this.startToken.length();
			int end = src.indexOf(this.endToken, i);
			if (end == -1) {
				context.setResult(
					ErrorToken.ofString(src.substring(index), index),
					src.length() - index
				);
				return;
			}

			String body = src.substring(i, end);
			context.setResult(
				new CommentToken(body, index),
				end + this.endToken.length() - index
			);
		}
	}
}
