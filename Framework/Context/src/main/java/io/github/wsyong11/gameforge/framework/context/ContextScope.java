package io.github.wsyong11.gameforge.framework.context;

import org.jetbrains.annotations.NotNull;
/**
 * 表示一次上下文作用域。
 *
 * <p>通常通过 {@code Context.begin(...)} 等方法创建，
 * 并配合 {@code try-with-resources} 语句自动关闭：</p>
 *
 * <pre>{@code
 * try (ContextScope scope = Context.begin(ctx)) {
 *     // 在此作用域内 ctx 为当前上下文
 * }
 * }</pre>
 *
 * <p>作用域具有严格的线程与生命周期约束：</p>
 *
 * <ul>
 *   <li>只能在创建该 Scope 的线程中调用 {@link #close()}。</li>
 *   <li>{@link #close()} 只能调用一次。</li>
 * </ul>
 *
 * <p>违反约束时将抛出异常：</p>
 *
 * <ul>
 *   <li>重复调用 {@link #close()} 会抛出 {@link IllegalStateException}。</li>
 *   <li>跨线程调用 {@link #close()} 会抛出 {@link IllegalCallerException}。</li>
 * </ul>
 *
 * <p>该接口用于维护上下文栈的一致性，
 * 不应被实现类以外的代码手动干预其生命周期。</p>
 */
public interface ContextScope extends AutoCloseable{
	@Override
	void close();
}
