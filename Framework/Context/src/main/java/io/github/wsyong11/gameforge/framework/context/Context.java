package io.github.wsyong11.gameforge.framework.context;

import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.framework.context.annotation.UsingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Context {
	public static void setGlobal(@Nullable Context ctx) {
		ContextStack.setGlobal(ctx);
	}

	@Nullable
	public static Context getGlobal() {
		return ContextStack.getGlobal();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static void begin(@NotNull Context ctx, @NotNull Runnable action) {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(action, "action is null");

		try (ContextScope ignored = scope(ctx)) {
			action.run();
		}
	}

	public static <T> T begin(@NotNull Context ctx, @NotNull Supplier<T> action) {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(action, "action is null");

		try (ContextScope ignored = scope(ctx)) {
			return action.get();
		}
	}

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

	// -------------------------------------------------------------------------------------------------------------- //

	@ThreadSensitive
	@NotNull
	public static ContextScope scope(@NotNull Context ctx) {
		Objects.requireNonNull(ctx, "ctx is null");

		ContextStack stack = ContextStack.getInstance();
		return new ContextScopeImpl(stack, ctx);
	}

	@UsingContext(require = true)
	@NotNull
	public static Context get() {
		Context context = getOptional();
		if (context == null)
			throw new IllegalStateException("Context not found");
		return context;
	}

	@UsingContext
	@Nullable
	public static Context getOptional() {
		ContextStack stack = ContextStack.getInstance();
		return stack.getCurrent();
	}

	// -------------------------------------------------------------------------------------------------------------- //

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

	@UsingContext
	public static boolean isEffectiveDebug() {
		return ContextStack.getInstance().isEffectiveDebug();
	}

	public abstract boolean isDebug();
}
