package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import io.github.wsyong11.gameforge.framework.lang.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.lang.token.*;
import org.apache.commons.lang3.ArrayUtils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class JsonPathElementParser {
	private static final Tokenizer JSON_PATH_TOKENIZER = new Tokenizer(List.of(
		// 1. 多字符运算符/递归操作符（.., <=, >=, !=, ==）
		TokenRules.ofOperator(Set.of("==", "!=", "<=", ">=", "<", ">", ".", "[", "]", "(", ")", "?", "@", "$", "*", ",", ":")),

		// 2. 关键字（true, false, null）
		TokenRules.ofKeywords(Set.of("true", "false", "null")),

		// 3. 数字
		TokenRules.ofNumber(),

		// 4. 字符串（单引号/双引号）
		TokenRules.ofString(Set.of((int) '\'', (int) '"')),

		// 5. 标识符（属性名）
		TokenRules.ofKeywords(Set.of()), // 空集，表示所有未匹配的标识符使用 IdentToken 捕获

		// 6. 注释（可选，JSONPath 可以忽略注释）
		TokenRules.ofComment("//"),
		TokenRules.ofMultiLineComment("/*", "*/")
	));

	private final String path;
	private ElementPath instance;

	public JsonPathElementParser(@NotNull @Language("JSONPath") String path) {
		Objects.requireNonNull(path, "path is null");

		this.path = path;
		this.instance = null;
	}

	@NotNull
	public synchronized ElementPath parse() {
		if (this.instance != null)
			return this.instance;

		List<JsonPathOperation> operations = new ArrayList<>();

		TokenIterator tokenIterator = new TokenIterator(JSON_PATH_TOKENIZER.tokenize(this.path));

		int index = 0;
		while (true) {
			Token token = tokenIterator.next();

			if (token instanceof ErrorToken)
				throw this.newSyntaxError(token, "Invalid syntax");

			if (token instanceof EOFToken)
				break;

			if (token instanceof CommentToken)
				continue;

			System.out.println("Token: " + token);
			if (!this.processToken(index, token, tokenIterator, operations))
				throw this.newSyntaxError(token, "Invalid syntax");

			index++;
		}

		return new JsonPathElementPath(operations);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private static final Pattern JSON_FIELD_REGEX = Pattern.compile("^[A-Za-z0-9_]+$");

	@NotNull
	private SyntaxException newSyntaxError(@NotNull Token token, @NotNull String message, Object... formatArgs) {
		Objects.requireNonNull(token, "token is null");
		Objects.requireNonNull(message, "message is null");
		Objects.requireNonNull(formatArgs, "formatArgs is null");

		return new SyntaxException(message.formatted(formatArgs), this.path, token.getSrcIndex(), token.length());
	}

	@NotNull
	private SyntaxException newExpectedStyleError(@NotNull Token token, @NotNull String... expectChars) {
		return this.newSyntaxError(token, "Expected %s, but '%s' found",
			expectChars.length == 1
				? "'" + expectChars[0] + "'"
				: "%s or '%s'".formatted(
				/**/String.join(", ", ArrayUtils.remove(expectChars, expectChars.length - 1)) + "",
				/**/expectChars[expectChars.length - 1]),
			token.getToken());
	}

	@NotNull
	@Contract("_ -> param1")
	private Token checkToken(@NotNull Token token) {
		Objects.requireNonNull(token, "token is null");

		if (token instanceof EOFToken)
			throw new SyntaxException("Unexpected end of input", this.path, this.path.length(), 1);

		if (token instanceof ErrorToken)
			throw this.newSyntaxError(token, "Invalid syntax");

		return token;
	}

	private int parseInt(@NotNull Token token) {
		if (!token.equalsToken(NumberToken.class))
			throw this.newSyntaxError(token,
				"Excepted number, but '%s' found",
				token.getToken());

		try {
			return Integer.parseInt(token.getToken());
		} catch (NumberFormatException e) {
			SyntaxException exception = this.newSyntaxError(token,
				"'%s' cannot parse as integer",
				token.getToken());
			exception.initCause(e);
			throw exception;
		}
	}

	private boolean processToken(int index, @NotNull Token token, @NotNull TokenIterator tokenIterator, @NotNull List<JsonPathOperation> operations) {
		if (index == 0) {
			this.processRoot(tokenIterator, operations);
			return true;
		}

		if (token.equalsToken(OperatorToken.class, ".")) {
			return this.processDot(tokenIterator, operations);
		}

		return false;
	}

	private void processRoot(@NotNull TokenIterator iterator, @NotNull List<JsonPathOperation> operations) {
		Token token = this.checkToken(iterator.getCurrent());
		if (token.equalsToken(OperatorToken.class)) {
			if (token.equalsToken("$")) {
				operations.add(new JsonPathOperation.Root());
				return;
			}
			if (token.equalsToken("@")) {
				operations.add(new JsonPathOperation.Self());
				return;
			}
		}

		throw this.newSyntaxError(token,
			"JSONPath must start with '$' or '@', but '%s' found",
			token.getToken());
	}

	private boolean processDot(@NotNull TokenIterator iterator, @NotNull List<JsonPathOperation> operations) {
		assert iterator.getCurrent().equalsToken(OperatorToken.class, ".");
		Token token = this.checkToken(iterator.next());
		// .field
		if (token.equalsToken(IdentToken.class)) {
			String fieldName = token.getToken();
			if (!JSON_FIELD_REGEX.matcher(fieldName).matches())
				throw new AssertionError();

			operations.add(new JsonPathOperation.AccessField(JsonPathOperation.Predicate.withField(fieldName)));
			return true;
		}

		// .*
		if (token.equalsToken(OperatorToken.class, "*")) {
			operations.add(new JsonPathOperation.Wildcard());
			return true;
		}

		// ..
		if (token.equalsToken(OperatorToken.class, ".")) {
			Token nextToken = this.checkToken(iterator.next());

			JsonPathOperation.Predicate predicate;
			// ..[...]
			if (nextToken.equalsToken(OperatorToken.class, "[")) {
				predicate = this.processBrackets(iterator);
				if (predicate == null)
					return false;
				// ..field
			} else if (nextToken.equalsToken(IdentToken.class)) {
				predicate = JsonPathOperation.Predicate.withField(nextToken.getToken());
			} else {
				throw this.newSyntaxError(nextToken, "Expected field name or '[' after '..'");
			}

			operations.add(new JsonPathOperation.RecursiveAccessField(predicate));
			return true;
		}

		return false;
	}

	@Nullable
	private JsonPathOperation.Predicate processBrackets(@NotNull TokenIterator iterator) {
		Token startToken = iterator.getCurrent();
		assert startToken.equalsToken(OperatorToken.class, "[");

		Token token = this.checkToken(iterator.next());
		// ["..."] or [0] or [0:1:1]
		if (token.equalsToken(StringToken.class) || token.equalsToken(NumberToken.class)) {
			// [0] or [0:1:1], 使用 while 来实现 , 解析
			// 如果为 slice 则直接return
			while (true) {
				Token nextToken = this.checkToken(iterator.next());
				if (currentToken.equalsToken(NumberToken.class)) {
					// token = Index or slice start

					// [0]

					if (nextToken.equalsToken(OperatorToken.class, "]"))
						return JsonPathOperation.Predicate.withIndex(this.parseInt(token));
				}

				if (nextToken.equalsToken(OperatorToken.class, ","))
					continue;

				return null;
			}
//			List<JsonPathOperation.Predicate> predicates = new ArrayList<>();
//			predicates.add(this.processStringAndNumberPredicate(token));
//
//			Token nextToken = this.checkToken(iterator.next());
//			//
//			if (nextToken.equalsToken(OperatorToken.class, ",")) {
//				while ()
//			} else if (!nextToken.equalsToken(OperatorToken.class, "]")) {
//				throw this.newSyntaxError(startToken,
//					"Bracket not closed, '%s' found",
//					nextToken.getToken());
//			}
//
//			return
		}

		return null;
	}

	private void checkBracketEnd(@NotNull Token startBracketToken, @NotNull Token token) {
		if (!token.equalsToken(OperatorToken.class, "]")) {
			throw this.newSyntaxError(startBracketToken,
				"Bracket not closed, '%s' found",
				token.getToken());
		}
	}

	private static class FilterParser {

	}
}
