package io.github.wsyong11.gameforge.framework.lang.ast.parser;

import io.github.wsyong11.gameforge.framework.lang.ast.node.ASTNode;
import io.github.wsyong11.gameforge.framework.lang.token.Token;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;

public interface ASTParserStrategy {
	/**
	 * 尝试解析当前 token
	 *
	 * @return 成功时返回 AST 节点，否则返回 null
	 * @throws ParseException 如果发现错误但无法恢复
	 */
	@Nullable
	ASTNode parse() throws ParseException;

	/**
	 * 判断这个策略是否适用于当前 token
	 */
	boolean matches(Token token);
}
