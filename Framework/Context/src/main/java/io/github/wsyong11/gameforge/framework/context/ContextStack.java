package io.github.wsyong11.gameforge.framework.context;

import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.framework.context.annotation.UsingContext;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

@Internal
public class ContextStack {
	private static final Logger LOGGER = Log.getLogger();

	private static final ThreadLocal<ContextStack> INSTANCE = ThreadLocal.withInitial(ContextStack::new);

	@ThreadSensitive
	@NotNull
	public static ContextStack getInstance() {
		return INSTANCE.get();
	}

	private final Deque<StackItem> stack;

	public ContextStack() {
		this.stack = new LinkedList<>();
	}

	public void push(@NotNull Context ctx) {
		Objects.requireNonNull(ctx, "ctx is null");

		StackItem parentItem = this.stack.peek();

		boolean parentDebug = parentItem != null && parentItem.isDebug();
		boolean debug = ctx.isDebug();
		Thread owner = Thread.currentThread();

		if (parentDebug || debug)
			LOGGER.trace("Push context in thread {}: {}", owner, lazy(ctx));

		StackItem item = new StackItem(
			ctx,
			owner,
			debug,
			parentDebug
		);

		this.stack.push(item);
	}

	public void pop(@NotNull Context ctx) {
		Objects.requireNonNull(ctx, "ctx is null");

		StackItem item = this.stack.peek();
		if (item == null)
			throw new IllegalStateException("Context stack is empty");

		Thread currentThread = Thread.currentThread();
		Thread thread = item.getOwner();
		if (thread != currentThread)
			throw new IllegalCallerException("Context push in thread" + thread + ", but pop thread is " + currentThread);

		Context itemContext = item.getContext();
		if (itemContext != ctx)
			throw new IllegalArgumentException("Context in stack " + itemContext + " is not a context " + ctx);

		this.stack.pop();
	}

	@Nullable
	public Context getCurrent() {
		StackItem item = this.getCurrentItem();
		return item == null ? null : item.getContext();
	}

	@Nullable
	protected StackItem getCurrentItem() {
		return this.stack.peek();
	}

	public boolean isDebug() {
		StackItem item = this.getCurrentItem();
		return item != null && item.isDeclaredDebug();
	}

	@NotNull
	@Unmodifiable
	public Stream<Context> getStackSnapshot() {
		List<StackItem> snapshot = List.copyOf(this.stack);

		return snapshot
			.stream()
			.map(StackItem::getContext);
	}

	public int size() {
		return this.stack.size();
	}

	protected static class StackItem {
		private final Context context;
		private final Thread owner;

		private final boolean debug;
		private final boolean parentDebug;
		@Nullable
		private final DebugInfo debugInfo;

		public StackItem(@NotNull Context context, @NotNull Thread owner, boolean debug, boolean parentDebug) {
			Objects.requireNonNull(context, "context is null");
			this.context = context;
			this.owner = owner;

			this.debug = debug;
			this.parentDebug = parentDebug;

			if (this.isDeclaredDebug()) {
				this.debugInfo = DebugInfo.of(owner);
			} else {
				this.debugInfo = null;
			}
		}

		@NotNull
		public Context getContext() {
			return this.context;
		}

		@NotNull
		public Thread getOwner() {
			return this.owner;
		}

		public boolean isDebug() {
			return this.debug;
		}

		public boolean isDeclaredDebug() {
			return this.debug || this.parentDebug;
		}

		@Nullable
		public DebugInfo getDebugInfo() {
			return this.debugInfo;
		}
	}
}
