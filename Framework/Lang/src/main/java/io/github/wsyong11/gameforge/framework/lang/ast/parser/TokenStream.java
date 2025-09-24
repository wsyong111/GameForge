package io.github.wsyong11.gameforge.framework.lang.ast.parser;

import io.github.wsyong11.gameforge.framework.lang.ast.parser.error.CompileReportCollector;
import io.github.wsyong11.gameforge.framework.lang.token.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Set;

public interface TokenStream {
	boolean hasNext();           // 还有没有 token？

	@NotNull
	Token next();                // 消费并返回下一个 token

	@NotNull
	Token peek();                // 看一下当前 token（不消费）

	@NotNull
	Token peek(int offset);      // 看后面第 n 个 token

	int position();              // 当前解析位置

	void rewind(int pos);        // 回退到指定位置

	// -------------------------------------------------------------------------------------------------------------- //

	boolean match(@NotNull Class<? extends Token> type);        // 如果是这个类型，就消费并返回 true

	boolean match(@NotNull String literal);        // 如果是这个字面符号，就消费并返回 true

	@NotNull
	Token expect(@NotNull Class<? extends Token> type, @NotNull CompileReportCollector errors);

	@NotNull
	Token expect(String literal, @NotNull CompileReportCollector errors);

	void syncUntil(@NotNull Set<Class<? extends Token>> syncPoints);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@UnmodifiableView
	List<Token> subList(int from, int to);
}
