package io.github.wsyong11.gameforge.framework.context;

import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import io.github.wsyong11.gameforge.framework.context.annotation.UsingContext;
import io.github.wsyong11.gameforge.util.exception.ExceptionRunnable;
import io.github.wsyong11.gameforge.util.exception.ExceptionSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 上下文管理类，用于管理线程内上下文栈以及全局上下文。
 *
 * <p>主要功能：
 * <ul>
 *     <li>线程内上下文栈管理：支持 push/pop 以及 {@code try-with-resources} 作用域</li>
 *     <li>全局上下文：可在所有线程访问，支持动态更新</li>
 *     <li>隐藏/禁用上下文：可创建作用域来屏蔽上层上下文或暂时禁用上下文</li>
 *     <li>调试状态传播：Debug 状态可沿上下文栈向下传播，并在必要时记录线程堆栈信息</li>
 *     <li>异步任务上下文继承：提供 {@code inherit} 方法包装 {@link Runnable}、{@link Supplier} 等</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre><code>
 * // 普通作用域
 * try (ContextScope scope = Context.scope(ctx)) {
 *     // 在 ctx 上下文中执行逻辑
 * }
 *
 * // 隐藏作用域，屏蔽上层上下文
 * try (ContextScope scope = Context.scopeHidden(ctx)) {
 *     ...
 * }
 *
 * // 异步任务绑定上下文
 * Runnable task = Context.inherit(ctx, () -> doSomething());
 * executor.submit(task);
 * </code></pre>
 *
 * <p>注意事项：
 * <ul>
 *     <li>上下文作用域必须在 {@code try-with-resources} 中使用，否则可能导致上下文未正确恢复</li>
 *     <li>隐藏作用域仅屏蔽上层上下文的访问，不影响全局上下文</li>
 *     <li>禁用作用域完全禁用上下文访问，直到再次通过 {@link #scope(Context)} 或其他方法添加上下文</li>
 *     <li>全局上下文修改即时生效，可能影响所有线程，使用时需注意线程安全</li>
 * </ul>
 */
// TODO: 2026/02/44 Impl full hidden scope
public abstract class Context {
	/**
	 * 设置全局上下文。
	 *
	 * <p>全局上下文会作为默认上下文模板，在<strong>线程首次访问上下文系统时</strong>
	 * 被复制为该线程的初始上下文快照。</p>
	 *
	 * <p style="color: yellow">注意：此方法不会立即影响已经访问过上下文系统的线程。
	 * 已存在的线程将继续使用其当前的全局上下文快照。</p>
	 *
	 * <p>若需要强制将新的全局上下文同步到所有已初始化的线程，
	 * 请调用 {@link #forceUpdateGlobal()}。</p>
	 *
	 * @param ctx 要设置的全局上下文，允许为 {@code null} 表示清空全局上下文
	 */
	public static void setGlobal(@Nullable Context ctx) {
		ContextStack.setGlobal(ctx);
	}

	/**
	 * 获取当前全局上下文。
	 *
	 * <p>返回最近一次通过 {@link #setGlobal(Context)} 设置的全局上下文。</p>
	 *
	 * <p>该方法返回的是全局模板本身，而不是线程的上下文快照。</p>
	 *
	 * @return 当前全局上下文，如果未设置则返回 {@code null}
	 */
	@Nullable
	public static Context getGlobal() {
		return ContextStack.getGlobal();
	}

	/**
	 * 强制将当前全局上下文同步到所有已初始化线程。
	 *
	 * <p>此操作会覆盖各线程保存的全局上下文快照，使其与当前全局上下文一致。</p>
	 *
	 * <p style="color: yellow">警告：该方法可能影响正在运行的线程逻辑，
	 * 通常仅应在框架启动阶段或明确的系统级重配置场景中使用。</p>
	 *
	 * <p style="color: yellow"><u>该 API 属于低级操作，滥用可能导致不可预期的行为。</u></p>
	 */
	@UnsafeAPI
	public static void forceUpdateGlobal() {
		ContextStack.forceUpdateGlobal();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 在指定上下文中执行操作，并在执行完成后自动恢复原上下文。
	 *
	 * <p>等价于：
	 * <pre><code>
	 * try (ContextScope ignored = scope(ctx)) {
	 *     action.run();
	 * }
	 * </code></pre>
	 *
	 * <p>如果操作抛出异常，将原样向上抛出。
	 *
	 * @param ctx    要激活的上下文，不能为空
	 * @param action 要执行的操作
	 * @param <E>    可能抛出的异常类型
	 * @throws E 操作抛出的异常
	 */
	@ThreadSensitive
	public static <E extends Throwable> void begin(@NotNull Context ctx, @NotNull ExceptionRunnable<E> action) throws E {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(action, "action is null");

		try (ContextScope ignored = scope(ctx)) {
			action.run();
		}
	}

	/**
	 * 在指定上下文中执行操作并返回结果，
	 * 执行完成后自动恢复原上下文。
	 *
	 * <p>等价于：
	 * <pre><code>
	 * try (ContextScope ignored = scope(ctx)) {
	 *     return action.get();
	 * }
	 * </code></pre>
	 *
	 * @param ctx    要激活的上下文，不能为空
	 * @param action 要执行的操作
	 * @param <T>    返回值类型
	 * @param <E>    可能抛出的异常类型
	 * @return 操作返回的结果
	 * @throws E 操作抛出的异常
	 */
	@ThreadSensitive
	public static <T, E extends Throwable> T begin(@NotNull Context ctx, @NotNull ExceptionSupplier<T, E> action) throws E {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(action, "action is null");

		try (ContextScope ignored = scope(ctx)) {
			return action.get();
		}
	}

	/**
	 * 将指定上下文绑定到 {@link Runnable}。
	 *
	 * <p>返回的 {@code Runnable} 在执行时会自动激活该上下文，
	 * 执行结束后恢复执行前的上下文状态。
	 *
	 * <p>常用于在线程池或异步任务中显式传播上下文。
	 *
	 * @param ctx    要绑定的上下文
	 * @param action 原始任务
	 * @return 包装后的任务
	 */
	@NotNull
	public static Runnable inherit(@NotNull Context ctx, @NotNull Runnable action) {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(action, "action is null");

		return () -> {
			try (ContextScope ignored = scope(ctx)) {
				action.run();
			}
		};
	}

	/**
	 * 将指定上下文绑定到带异常的 {@code ExceptionRunnable}。
	 *
	 * <p>执行时自动激活上下文，结束后恢复，
	 * 异常将原样向上抛出。
	 *
	 * @see #inherit(Context, Runnable)
	 */
	@NotNull
	public static <E extends Throwable> ExceptionRunnable<E> inheritExceptionable(@NotNull Context ctx, @NotNull ExceptionRunnable<E> action) {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(action, "action is null");

		return () -> {
			try (ContextScope ignored = scope(ctx)) {
				action.run();
			}
		};
	}

	/**
	 * 将指定上下文绑定到 {@link Supplier}。
	 *
	 * <p>执行时自动激活上下文，结束后恢复。
	 *
	 * @param ctx    要绑定的上下文
	 * @param action 原始操作
	 * @param <T>    返回值类型
	 * @return 包装后的 Supplier
	 */
	@NotNull
	public static <T> Supplier<T> inherit(@NotNull Context ctx, @NotNull Supplier<T> action) {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(action, "action is null");

		return () -> {
			try (ContextScope ignored = scope(ctx)) {
				return action.get();
			}
		};
	}

	/**
	 * 将指定上下文绑定到带异常的 {@code ExceptionSupplier}。
	 *
	 * <p>执行时自动激活上下文，结束后恢复，
	 * 异常将原样向上抛出。
	 *
	 * @see #inherit(Context, Supplier)
	 */
	@NotNull
	public static <T, E extends Throwable> ExceptionSupplier<T, E> inheritExceptionable(@NotNull Context ctx, @NotNull ExceptionSupplier<T, E> action) {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(action, "action is null");

		return () -> {
			try (ContextScope ignored = scope(ctx)) {
				return action.get();
			}
		};
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 创建普通上下文作用域。
	 *
	 * <p>在该作用域内，指定上下文将成为当前上下文。
	 *
	 * <p>如果上层上下文已处于调试模式，
	 * 则调试状态会自动向下传播。
	 *
	 * <p>必须在 {@code try-with-resources} 中使用。
	 *
	 * @param ctx 要激活的上下文
	 * @return 作用域对象
	 */
	@ThreadSensitive
	@NotNull
	public static ContextScope scope(@NotNull Context ctx) {
		Objects.requireNonNull(ctx, "ctx is null");

		ContextStack stack = ContextStack.getInstance();
		return new Scope.Normal(stack, ctx);
	}

	/**
	 * 创建隐藏上下文作用域。
	 *
	 * <p>隐藏作用域会屏蔽其之上的上下文，
	 * 仅允许访问当前作用域及其以下的上下文。
	 *
	 * <p>传入 {@code null} 表示不添加新上下文，
	 * 但仍会隐藏上层上下文。
	 *
	 * <p>全局上下文不受隐藏作用域影响。
	 *
	 * @param ctx 可选的新上下文
	 * @return 隐藏作用域对象
	 */
	@ThreadSensitive
	@NotNull
	public static ContextScope scopeHidden(@Nullable Context ctx) {
		ContextStack stack = ContextStack.getInstance();
		return new Scope.Hidden(stack, ctx);
	}

	/**
	 * 创建禁用上下文作用域。
	 *
	 * <p>在该作用域内，上下文访问被禁用，
	 * 直到再次通过 {@link #scope(Context)} 等方法显式添加上下文。
	 *
	 * @return 禁用作用域对象
	 */
	@ThreadSensitive
	@NotNull
	public static ContextScope scopeDisable() {
		ContextStack stack = ContextStack.getInstance();
		return new Scope.Disable(stack);
	}

	/**
	 * 获取当前上下文。
	 *
	 * <p>如果当前线程未绑定上下文，
	 * 且未设置全局上下文，则抛出异常。
	 *
	 * @return 当前上下文
	 * @throws IllegalStateException 未找到上下文时抛出
	 */
	@UsingContext(require = true)
	@NotNull
	public static Context get() {
		Context context = getOptional();
		if (context == null)
			throw new IllegalStateException("Context not found");
		return context;
	}

	/**
	 * 获取当前上下文。
	 *
	 * <p>若不存在本地上下文，则返回全局上下文；
	 * 若全局上下文也未设置，则返回 {@code null}。
	 *
	 * @return 当前上下文或 {@code null}
	 */
	@UsingContext
	@Nullable
	public static Context getOptional() {
		ContextStack stack = ContextStack.getInstance();
		return stack.getCurrent();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 按从近到远（栈顶到栈底）的顺序，
	 * 查找第一个满足条件的上下文。
	 *
	 * <p>隐藏或禁用作用域会影响可见范围。
	 *
	 * @param predicate 过滤条件
	 * @return 匹配的上下文，未找到则返回 {@code null}
	 */
	@UsingContext
	@Nullable
	public static Context getEach(@NotNull Predicate<Context> predicate) {
		Objects.requireNonNull(predicate, "predicate is null");

		ContextStack stack = ContextStack.getInstance();
		return stack
			.getStackSnapshot()
			.filter(predicate)
			.findFirst()
			.orElse(null);
	}

	/**
	 * 按从近到远（栈顶到栈底）的顺序，
	 * 获取所有满足条件的上下文。
	 *
	 * @param predicate 过滤条件
	 * @return 不可修改的上下文列表
	 */
	@UsingContext
	@NotNull
	@Unmodifiable
	public static List<Context> getEachList(@NotNull Predicate<Context> predicate) {
		Objects.requireNonNull(predicate, "predicate is null");

		ContextStack stack = ContextStack.getInstance();
		return stack
			.getStackSnapshot()
			.filter(predicate)
			.toList();
	}

	/**
	 * 按从近到远（栈顶到栈底）的顺序，
	 * 获取当前上下文堆栈的快照并作为流返回
	 *
	 * @return 上下文流
	 */
	@UsingContext
	@NotNull
	public static Stream<Context> getEachStream() {
		ContextStack stack = ContextStack.getInstance();
		return stack.getStackSnapshot();
	}

	/**
	 * 获取当前线程的上下文栈大小。
	 *
	 * <p>不包含全局上下文。
	 *
	 * @return 当前栈深度
	 */
	@ThreadSensitive
	public static int getStackSize() {
		return ContextStack.getInstance().size();
	}

	// -------------------------------------------------------------------------------------------------------------- //

//	@UsingContext
//	public static void dump(@NotNull Consumer<String> logger) {
//		Objects.requireNonNull(logger, "logger is null");
//
//		ContextStack stack = ContextStack.getInstance();
//		stack.getStackSnapshot()
//	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 返回当前线程下的有效调试状态。
	 *
	 * <p>当以下任一条件满足时返回 {@code true}：
	 * <ul>
	 *     <li>当前上下文声明为调试模式</li>
	 *     <li>上层上下文处于调试模式</li>
	 *     <li>全局上下文处于调试模式</li>
	 * </ul>
	 *
	 * @return 是否处于有效调试状态
	 */
	@UsingContext
	public static boolean isEffectiveDebug() {
		return ContextStack.getInstance().isDebug();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final boolean debug;

	protected Context(boolean debug) {
		this.debug = debug;
	}

	/**
	 * 返回当前上下文自身声明的调试状态。
	 *
	 * <p>该值不包含上层或全局上下文传播的调试状态。
	 *
	 * @return 是否声明为调试模式
	 */
	public boolean isDebug() {
		return this.debug;
	}

	@NotNull
	public <T extends Context> T as(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		if (!type.isInstance(this))
			throw new IllegalStateException("Cannot get as " + type.getName());
		return type.cast(this);
	}

	@Nullable
	public <T extends Context> T asUnsafe(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		if (!type.isInstance(this))
			return null;
		return type.cast(this);
	}

	@NotNull
	public <T extends Context> Optional<T> asOptional(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		if (!type.isInstance(this))
			return Optional.empty();
		return Optional.of(type.cast(this));
	}
}
