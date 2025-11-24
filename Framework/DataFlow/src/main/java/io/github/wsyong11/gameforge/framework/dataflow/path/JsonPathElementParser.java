package io.github.wsyong11.gameforge.framework.dataflow.path;

import io.github.wsyong11.gameforge.framework.lang.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.lang.token.*;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class JsonPathElementParser {
	private static final Tokenizer JSON_PATH_TOKENIZER = new Tokenizer(List.of(
		// 1. 多字符运算符/递归操作符（.., <=, >=, !=, ==）
		TokenRules.ofOperator(Set.of("$", "..", "==", "!=", "<=", ">=", "<", ">", ".", "[", "]", "(", ")", "?", "@", "*", ",", ":")),

		// 2. 关键字（true, false, null）
		TokenRules.ofKeywords(Set.of("true", "false", "null")),

		// 3. 数字
		TokenRules.ofNumber(),

		// 4. 字符串（单引号/双引号）
		TokenRules.ofString(Set.of((int) '\'', (int) '"')),

		// 5. 标识符（属性名）
		TokenRules.ofKeywords(Set.of()), // 空集，表示所有未匹配的标识符也可以用 IdentToken 捕获

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
	public ElementPath parse() {
		if (this.instance != null)
			return this.instance;

		List<JsonPathElementPath.Operation> operations = new ArrayList<>();

		Iterator<Token> tokenIterator = JSON_PATH_TOKENIZER.tokenize(this.path);

		int index = 0;
		while (true) {
			Token token = tokenIterator.next();

			if (token instanceof ErrorToken)
				throw new SyntaxException("Invalid syntax", this.path, token.getSrcIndex(), token.length());

			if (token instanceof EOFToken) {
				break;
			}

			this.processToken(index, token, operations);

			System.out.println(token);
			index++;
		}

		return new JsonPathElementPath(operations);
	}

	private boolean processToken(int index, @NotNull Token token, @NotNull List<JsonPathElementPath.Operation> operations) {
		Objects.requireNonNull(token, "token is null");
		Objects.requireNonNull(operations, "operations is null");

		if (index == 0) {
			if (token instanceof OperatorToken) {
				if ("$".equals(token.getToken()))
					operations.add(new JsonPathElementPath.OpRoot());
			}
			return true;
		}

		return false;
	}
}
