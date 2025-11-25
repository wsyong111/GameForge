package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import io.github.wsyong11.gameforge.framework.lang.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.lang.token.*;
import org.apache.commons.lang3.ArrayUtils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

	private boolean processToken(int index, @NotNull Token token, @NotNull TokenIterator tokenIterator, @NotNull List<JsonPathOperation> operations) {
		if (index == 0) {
			tokenIterator.putToken(token);
			this.processRoot(tokenIterator, operations);
			return true;
		}

		if (token.equalsToken(OperatorToken.class, ".")) {
			tokenIterator.putToken(token);
			return this.processDot(tokenIterator, operations);
		}

//		if (token instanceof OperatorToken) {
//			if (index == 0) {
//				this.processRoot(token, operations);
//				return true;
//			}
//
//			if (token.equalsToken(".")) {
//				this.processDot(token, tokenIterator, operations);
//				return true;
//			}
//
//			if (token.equalsToken("..")) {
//				this.processRecursiveDot(token, tokenIterator, operations);
//				return true;
//			}
//
//			if (token.equalsToken("[")) {
//				this.processBracket(token, tokenIterator, operations);
//				return true;
//			}
//		}

		return false;
	}

	private void processRoot(@NotNull TokenIterator iterator, @NotNull List<JsonPathOperation> operations) {
		Token token = this.checkToken(iterator.next());
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
		iterator.next();
		Token token = this.checkToken(iterator.next());
		// .field
		if (token.equalsToken(IdentToken.class)) {
			String fieldName = token.getToken();
			if (!JSON_FIELD_REGEX.matcher(fieldName).matches())
				throw new AssertionError();

			operations.add(new JsonPathOperation.AccessField(fieldName));
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

			String fieldName;
			// ..["field"]
			if (nextToken.equalsToken(OperatorToken.class, "[")) {
				Token bracketNextToken = this.checkToken(iterator.next());
				if (!bracketNextToken.equalsToken(StringToken.class))
					throw this.newSyntaxError(token, "Property name after '..[' must be quoted");

				fieldName = bracketNextToken.getToken();

				Token bracketCloseToken = this.checkToken(iterator.next());
				if (!bracketCloseToken.equalsToken(OperatorToken.class, "]"))
					throw this.newSyntaxError(bracketCloseToken, "missing closing ']' for bracket");
				// ..field
			} else if (nextToken.equalsToken(IdentToken.class)) {
				fieldName = nextToken.getToken();
			} else {
				throw this.newSyntaxError(nextToken, "Expected field name or '[' after '..'");
			}

			operations.add(new JsonPathOperation.AccessField.RecursiveField(fieldName));
			return true;
		}

		return false;
	}

	private static class FilterParser {

	}
}
