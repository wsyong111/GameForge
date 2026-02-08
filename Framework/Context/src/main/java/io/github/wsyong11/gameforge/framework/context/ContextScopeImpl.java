package io.github.wsyong11.gameforge.framework.context;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class ContextScopeImpl implements ContextScope {
	private final ContextStack stack;
	private final Context context;

	ContextScopeImpl(@NotNull ContextStack stack, @NotNull Context context) {
		Objects.requireNonNull(stack, "stack is null");
		Objects.requireNonNull(context, "context is null");

		this.stack = stack;
		this.context = context;

		stack.push(context);
	}

	@NotNull
	@Override
	public Context getContext() {
		return this.context;
	}

	@Override
	public void close() {
		this.stack.pop(this.context);
	}
}
