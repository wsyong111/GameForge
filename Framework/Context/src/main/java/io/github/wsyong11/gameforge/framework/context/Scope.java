package io.github.wsyong11.gameforge.framework.context;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@UtilityClass
class Scope {
	static class Normal implements ContextScope {
		private final ContextStack stack;
		private final Context context;

		private boolean closed;

		Normal(@NotNull ContextStack stack, @NotNull Context context) {
			Objects.requireNonNull(stack, "stack is null");
			Objects.requireNonNull(context, "context is null");

			this.stack = stack;
			this.context = context;

			this.closed = false;

			stack.push(context);
		}

		@Override
		public void close() {
			if (this.closed)
				throw new IllegalStateException("This scope is closed");
			this.closed = true;
			this.stack.pop(this.context);
		}

		@Override
		public String toString() {
			return "ContextScope[thread=" + this.stack.getOwner() + ", " +
				"context=" + this.context + ", " +
				"closed=" + this.closed + "]";
		}
	}

	static class Hidden implements ContextScope {
		private final ContextStack stack;
		private final Context context;

		private boolean closed;

		Hidden(@NotNull ContextStack stack, @Nullable Context context) {
			Objects.requireNonNull(stack, "stack is null");

			this.stack = stack;
			this.context = context;

			this.closed = false;

			stack.pushHidden(context);
		}

		@Override
		public void close() {
			if (this.closed)
				throw new IllegalStateException("This scope is closed");
			this.closed = true;
			this.stack.popHidden(this.context);
		}

		@Override
		public String toString() {
			return "HiddenScope[thread=" + this.stack.getOwner() + ", " +
				"context=" + this.context + ", " +
				"closed=" + this.closed + "]";
		}
	}

	static class Disable implements ContextScope {
		private final ContextStack stack;

		private boolean closed;

		Disable(@NotNull ContextStack stack) {
			Objects.requireNonNull(stack, "stack is null");

			this.stack = stack;

			this.closed = false;

			stack.pushDisable();
		}

		@Override
		public void close() {
			if (this.closed)
				throw new IllegalStateException("This scope is closed");
			this.closed = true;
			this.stack.popDisable();
		}

		@Override
		public String toString() {
			return "DisableScope[thread=" + this.stack.getOwner() + ", " +
				"closed=" + this.closed + "]";
		}
	}
}
