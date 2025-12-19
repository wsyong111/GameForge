package io.github.wsyong11.gameforge.framework.dataflow.path.json;

import io.github.wsyong11.gameforge.framework.dataflow.path.ElementPath;
import io.github.wsyong11.gameforge.framework.lang.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.lang.parser.AbstractLineTokenParser;
import io.github.wsyong11.gameforge.framework.lang.token.*;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class JsonPathElementParser extends AbstractLineTokenParser<ElementPath> {
	private static final Tokenizer TOKENIZER = new Tokenizer(List.of(
		// 1. 多字符运算符/递归操作符（.., <=, >=, !=, ==）
		TokenRules.ofOperator(Set.of("=~", "==", "!=", "<=", ">=", "<", ">", "..", ".", "[", "]", "(", ")", "?", "@", "$", "*", ",", ":", "/")),

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

	private final JsonPathMode mode;

	public JsonPathElementParser(@NotNull @Language("JSONPath") String path, JsonPathMode mode) {
		super(TOKENIZER, path);
		Objects.requireNonNull(mode, "mode is null");

		this.mode = mode;
	}

	@NotNull
	@Override
	public ElementPath parse(@NotNull TokenIterator iterator) {
		if (!this.consumeIfMatch(OperatorToken.class, "$") &&
			!this.consumeIfMatch(OperatorToken.class, "@"))
			throw this.expectedTokenError(this.getCurrent(), "$", "@");

		if (this.isEOF())
			return new JsonPathElementPath(List.of(new JsonPathOperation.Root()));

		List<JsonPathOperation> operations = new ArrayList<>();
		operations.add(new JsonPathOperation.Root());

		while (true) {
			Token token = this.getCurrent();
			if (token instanceof ErrorToken)
				throw this.invalidTokenError(token);

			if (token instanceof EOFToken)
				break;

			if (token instanceof CommentToken)
				continue;

			if (!this.processToken(operations))
				throw this.invalidTokenError(token);
		}

		return new JsonPathElementPath(operations);
		// TODO: 2025/11/29 json path parser
	}

	private boolean processToken(@NotNull List<JsonPathOperation> operations) {
		Objects.requireNonNull(operations, "operations is null");

		if (this.matchCurrent(OperatorToken.class, "[")) {
			if (!this.processBracketFieldAccess(operations) &&
				!this.processArrayIndexAccess(operations) &&
				!this.processWildcard(operations)
			)
				throw this.expectedTokenError(
					this.peek(),
					"?", "'", "\"", "0-9", "*");

			return true;
		}

		if (this.matchCurrent(OperatorToken.class, ".")) {
			if (!this.processFieldAccess(operations) &&
				!this.processWildcard(operations)
			)
				throw this.expectedTokenError(
					this.peek(),
					"[^0-9][a-zA-Z0-9]+");
			return true;
		}

//		if (this.matchCurrent(OperatorToken.class, "..")) {
//			if (!this.processRecursiveAccess(operations))
//				throw this.expectedTokenError(
//					this.peek(),
//					"[^0-9][a-zA-Z0-9]+");
//			return true;
//		}

		return false;
	}

	// .field
	private boolean processFieldAccess(@NotNull List<JsonPathOperation> operations) {
		Objects.requireNonNull(operations, "operations is null");

		if (!this.matchCurrent(OperatorToken.class, "."))
			return false;

		if (!this.matchNext(IdentToken.class))
			return false;

		Token field = this.next();
		operations.add(new JsonPathOperation.AccessField(JsonPathOperation.Predicate.withField(field.getToken())));

		this.nextUnsafe();
		return true;
	}

	// [*] and .*
	private boolean processWildcard(@NotNull List<JsonPathOperation> operations) {
		Objects.requireNonNull(operations, "operations is null");

		if (this.matchCurrent(OperatorToken.class, ".") &&
			this.matchNext(OperatorToken.class, "*")) {
			operations.add(new JsonPathOperation.Wildcard());
			this.next();
			this.nextUnsafe();
			return true;
		}

		if (this.matchCurrent(OperatorToken.class, "[") &&
			this.matchNext(OperatorToken.class, "*")) {
			this.next();
			this.next();
			if (!this.consumeIfMatch(OperatorToken.class, "]"))
				throw this.unmatchedBracketError();

			operations.add(new JsonPathOperation.Wildcard());
			return true;
		}

		return false;
	}

	// [0] and [0, ...]
	private boolean processArrayIndexAccess(@NotNull List<JsonPathOperation> operations) {
		Objects.requireNonNull(operations, "operations is null");

		if (!this.matchCurrent(OperatorToken.class, "["))
			return false;

		if (this.matchNext(OperatorToken.class, "*"))
			return false;

		if (!this.matchNext(NumberToken.class) && !this.matchNext(OperatorToken.class, ":"))
			return false;

		this.next();

		Set<Integer> indexSet = new LinkedHashSet<>();

		while (true) {
			if (this.matchCurrent(NumberToken.class)) {
				String indexString = this.getCurrentToken();
				int index;
				try {
					index = Integer.parseInt(indexString);
				} catch (NumberFormatException e) {
					SyntaxException exception = this.syntaxError("Failed to parse array index: '%s'", indexString);
					exception.initCause(e);
					throw exception;
				}

				indexSet.add(index);
				this.next();
				continue;
			}

			if (this.consumeIfMatch(OperatorToken.class, ","))
				continue;

			if (this.consumeIfMatch(OperatorToken.class, "]"))
				break;

			throw this.unmatchedBracketError();
		}

		operations.add(new JsonPathOperation.AccessField(JsonPathOperation.Predicate.withIndex(indexSet)));
		return true;
	}

	// ['field'] , ["field"] and ["field", ...]
	private boolean processBracketFieldAccess(@NotNull List<JsonPathOperation> operations) {
		Objects.requireNonNull(operations, "operations is null");

		if (!this.matchCurrent(OperatorToken.class, "["))
			return false;

		if (!this.matchNext(StringToken.class))
			return false;

		this.next();

		Set<String> fields = new LinkedHashSet<>();

		while (true) {
			if (this.matchCurrent(StringToken.class)) {
				String field = this.getCurrentToken();
				fields.add(field);
				this.next();
				continue;
			}

			if (this.consumeIfMatch(OperatorToken.class, ","))
				continue;

			if (this.consumeIfMatch(OperatorToken.class, "]"))
				break;

			throw this.unmatchedBracketError();
		}

		operations.add(new JsonPathOperation.AccessField(JsonPathOperation.Predicate.withFields(fields)));
		return true;
	}
}
//		List<JsonPathOperation> operations = new ArrayList<>();
//
//		int index = 0;
//		while (true) {
//			Token token = iterator.next();
//
//			if (token instanceof ErrorToken)
//				throw this.invalidTokenError(token);
//
//			if (token instanceof EOFToken)
//				break;
//
//			if (token instanceof CommentToken)
//				continue;
//
//			System.out.println("Token: " + token);
//			if (!this.processToken(index, operations))
//				throw this.invalidTokenError(token);
//
//			index++;
//		}
//
//		return new JsonPathElementPath(operations);
//	}
//
//	// -------------------------------------------------------------------------------------------------------------- //
//
//	private boolean processToken(int index, @NotNull List<JsonPathOperation> operations) {
//		if (index == 0) {
//			this.processRoot(operations);
//			return true;
//		}
//
//		// . 开头
//		if (this.matchCurrent(OperatorToken.class, ".")) {
//			return this.processDot(operations);
//		}
//
//		// [开头
//		if (this.matchCurrent(OperatorToken.class, "[")) {
//			JsonPathOperation.Predicate predicate = this.processBrackets();
//			if (predicate == null)
//				return false;
//
//			operations.add(new JsonPathOperation.AccessField(predicate));
//			return true;
//		}
//
//		return false;
//	}
//
//	private void processRoot(@NotNull List<JsonPathOperation> operations) {
//		if (this.matchCurrent(OperatorToken.class, "$")) {
//			operations.add(new JsonPathOperation.Root());
//			return;
//		}
//
//		if (this.matchCurrent(OperatorToken.class, "@")) {
//			operations.add(new JsonPathOperation.Self());
//			return;
//		}
//
//		throw this.expectedTokenError("$", "@");
//	}
//
//	private boolean processDot(@NotNull List<JsonPathOperation> operations) {
//		assert this.matchCurrent(OperatorToken.class, ".");
//		this.next();
//
//		// .field
//		if (this.matchCurrent(IdentToken.class)) {
//			String fieldName = this.getCurrentToken();
//			operations.add(new JsonPathOperation.AccessField(JsonPathOperation.Predicate.withField(fieldName)));
//			return true;
//		}
//
//		// .*
//		if (this.matchCurrent(OperatorToken.class, "*")) {
//			operations.add(new JsonPathOperation.Wildcard());
//			return true;
//		}
//
//		// ..
//		if (this.matchCurrent(OperatorToken.class, ".")) {
//			return this.processRecursiveDot(operations);
//		}
//
//		return false;
//	}
//
//	private boolean processRecursiveDot(@NotNull List<JsonPathOperation> operations) {
//		assert this.matchCurrent(OperatorToken.class, ".");
//		this.next();
//
//		JsonPathOperation.Predicate predicate;
//		// ..[...]
//		if (this.matchCurrent(OperatorToken.class, "[")) {
//			predicate = this.processBrackets();
//			if (predicate == null)
//				return false;
//			// ..field
//		} else if (this.matchCurrent(IdentToken.class)) {
//			predicate = JsonPathOperation.Predicate.withField(this.getCurrentToken());
//		} else {
//			throw this.expectedTokenError("field name", "[");
//		}
//
//		operations.add(new JsonPathOperation.RecursiveAccessField(predicate));
//		return true;
//	}
//
//	@Nullable
//	private JsonPathOperation.Predicate processBrackets() {
//		assert this.matchCurrent(OperatorToken.class, "[");
//		this.next();
//
//		if (this.consumeIfMatch(OperatorToken.class, "?")) {
//			if (!this.consumeIfMatch(OperatorToken.class, "("))
//				throw this.expectedTokenError("(");
//			this.rewind(3);
//			return new FilterParser(this.getText(), this.getIterator()).parse();
//		}
//
//		return null;
//	}
//
//	private static class FilterParser extends AbstractLineTokenParser<JsonPathOperation.Predicate> {
//		public FilterParser(@NotNull String text, @NotNull TokenIterator iterator) {
//			super(text, iterator);
//		}
//
//		@NotNull
//		@Override
//		protected JsonPathOperation.Predicate parse(@NotNull TokenIterator iterator) {
//			if (!this.consumeIfMatch(OperatorToken.class, "["))
//				throw this.expectedTokenError("[");
//
//			if (!this.consumeIfMatch(OperatorToken.class, "?"))
//				throw this.expectedTokenError("?");
//
//			if (!this.consumeIfMatch(OperatorToken.class, "("))
//				throw this.expectedTokenError("(");
//
//
//
//			return (context) -> false;
//		}
//	}