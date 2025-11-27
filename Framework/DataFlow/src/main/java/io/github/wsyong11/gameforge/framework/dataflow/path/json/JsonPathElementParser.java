package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import io.github.wsyong11.gameforge.framework.lang.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.lang.parser.AbstractLineTokenParser;
import io.github.wsyong11.gameforge.framework.lang.token.*;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class JsonPathElementParser extends AbstractLineTokenParser<ElementPath> {
	private static final Tokenizer TOKENIZER = new Tokenizer(List.of(
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

	public JsonPathElementParser(@NotNull @Language("JSONPath") String path) {
		super(TOKENIZER, path);
	}

	@NotNull
	public ElementPath parse(@NotNull TokenIterator iterator) {
		List<JsonPathOperation> operations = new ArrayList<>();

		int index = 0;
		while (true) {
			Token token = next();

			if (token instanceof ErrorToken)
				throw this.invalidTokenError(token);

			if (token instanceof EOFToken)
				break;

			if (token instanceof CommentToken)
				continue;

			System.out.println("Token: " + token);
			if (!this.processToken(index, token, iterator, operations))
				throw this.invalidTokenError(token);

			index++;
		}

		return  new JsonPathElementPath(operations);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private static final Pattern JSON_FIELD_REGEX = Pattern.compile("^[A-Za-z0-9_]+$");

	private int parseInt(@NotNull Token token) {
		if (!token.equalsToken(NumberToken.class))
			throw this.syntaxError(token,
				"Excepted number, but '%s' found",
				token.getToken());

		try {
			return Integer.parseInt(token.getToken());
		} catch (NumberFormatException e) {
			SyntaxException exception = this.syntaxError(token,
				"'%s' cannot parse as integer",
				token.getToken());
			exception.initCause(e);
			throw exception;
		}
	}

	private boolean processToken(int index, @NotNull Token token, @NotNull TokenIterator iterator, @NotNull List<JsonPathOperation> operations) {
		if (index == 0) {
			this.processRoot(iterator, operations);
			return true;
		}

		// . 开头
		if (token.equalsToken(OperatorToken.class, ".")) {
			return this.processDot(iterator, operations);
		}

		// [开头
		if (token.equalsToken(OperatorToken.class, "[")) {
			JsonPathOperation.Predicate predicate = this.processBrackets(iterator);
			if (predicate == null)
				return false;

			operations.add(new JsonPathOperation.AccessField(predicate));
			return true;
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

		throw this.expectedTokenError(token, "$", "@");
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
				throw this.expectedTokenError(nextToken, "field name", "[");
			}

			operations.add(new JsonPathOperation.RecursiveAccessField(predicate));
			return true;
		}

		return false;
	}

	private boolean processRecursiveDot(@NotNull List<JsonPathOperation> operations){

	}

	@Nullable
	private JsonPathOperation.Predicate processBrackets(@NotNull TokenIterator iterator) {
		Token startToken = iterator.getCurrent();
		assert startToken.equalsToken(OperatorToken.class, "[");

		Token token = this.checkToken(iterator.next());
		// ["..."] or [0] or [0:1:1]
		//  ^^^^^      ^      ^
		if (token.equalsToken(StringToken.class) || token.equalsToken(NumberToken.class)) {
			// [0] or [0:1:1]
			if (token.equalsToken(NumberToken.class)) {
				Token nextToken = this.checkToken(iterator.next());
				// Slice
				if (nextToken.equalsToken(OperatorToken.class, ":")) {
					iterator.rewind();
					iterator.rewind();
					return this.processSlice(iterator);
				} else {
					this.checkBracketEnd(nextToken);
				}

				int index = this.parseInt(token);
				return JsonPathOperation.Predicate.withIndex(index);
			}

			if (token.equalsToken(StringToken.class)) {
				Token nextToken = this.checkToken(iterator.next());
				this.checkBracketEnd(nextToken);

				String field = token.getToken();
				return JsonPathOperation.Predicate.withField(field);
			}
		}

		if (token.equalsToken(OperatorToken.class)) {
			iterator.rewind();
			return this.processSlice(iterator);
		}

		return null;
	}

	private void checkBracketEnd(@NotNull Token token) {
		if (!token.equalsToken(OperatorToken.class, "]")) {
			throw this.newSyntaxError(token,
				"Brackets are not closed, '%s' found",
				token.getToken());
		}
	}

	@NotNull
	private JsonPathOperation.Predicate processSlice(@NotNull TokenIterator iterator) {
		Token startBracketToken = iterator.getCurrent();
		assert startBracketToken.equalsToken(OperatorToken.class, "[");

		iterator.next(); // skip '['

		Integer start = null;
		Integer end = null;
		Integer step = null;

		// Helper: 解析可为空的数字
		java.util.function.Supplier<Integer> parseIntOrNull = () -> {
			Token t = iterator.getCurrent();
			if (t instanceof NumberToken num) {
				iterator.next();
				return num.getValue().intValue();
			}
			return null;
		};

		// 第一段：start 或 空
		if (!iterator.getCurrent().equalsToken(OperatorToken.class, ":")) {
			start = parseIntOrNull.get();
		}

		// 必须是 ':'
		expect(iterator, ":");
		iterator.next();

		// 第二段：end 或 空
		if (!iterator.getCurrent().equalsToken(OperatorToken.class, ":")
			&& !iterator.getCurrent().equalsToken(OperatorToken.class, "]")) {
			end = parseIntOrNull.get();
		}

		// 如果遇到第二个 ':'
		if (iterator.getCurrent().equalsToken(OperatorToken.class, ":")) {
			iterator.next(); // skip ':'

			// 第三段：step 或 空
			if (!iterator.getCurrent().equalsToken(OperatorToken.class, "]")) {
				step = parseIntOrNull.get();
			}
		}

		// 必须遇到 ']'
		expect(iterator, "]");
		iterator.next();

		// 默认值应用
		int s = start != null ? start : 0;
		int e = end != null ? end : Integer.MAX_VALUE;
		int st = step != null ? step : 1;

		return JsonPathOperation.Predicate.withSlice(s, e, st);
	}

	private void expect(TokenIterator it, String symbol) {
		if (!it.getCurrent().equalsToken(OperatorToken.class, symbol))
			throw this.newSyntaxError(it.getCurrent(), "Expected '" + symbol + "', but got: " + it.getCurrent());
	}

	private static class FilterParser {

	}
}
