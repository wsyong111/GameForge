package io.github.wsyong11.gameforge.framework.context;

import io.github.wsyong11.gameforge.framework.annotation.CallerSensitive;
import io.github.wsyong11.gameforge.framework.annotation.Internal;
import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.debug.ThreadSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.currentStackTrace;
import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

@Internal
class ContextStack {
	private static final Logger LOGGER = Log.getLogger();

	@Nullable
	private static volatile Context globalContext;

	private static final ThreadLocal<ContextStack> INSTANCE = ThreadLocal.withInitial(ContextStack::new);
	private static final Map<Thread, ContextStack> INSTANCE_MAP = Collections.synchronizedMap(new WeakHashMap<>());

	public static void setGlobal(@Nullable Context ctx) {
		if (globalContext != null)
			LOGGER.warn("Global context replaced to {}\n{}", lazy(ctx), currentStackTrace());

		globalContext = ctx;
	}

	@Nullable
	public static Context getGlobal() {
		return globalContext;
	}

	@UnsafeAPI
	public static void forceUpdateGlobal() {
		for (ContextStack stack : List.copyOf(INSTANCE_MAP.values()))
			stack.updateGlobal();
	}

	@ThreadSensitive
	@NotNull
	public static ContextStack getInstance() {
		return INSTANCE.get();
	}

//	@NotNull
//	@Unmodifiable
//	public static List<ContextDumpInfo> dumpAll() {
//		List<ContextStack> stacks = List.copyOf(INSTANCE_MAP.values());
//
//		return stacks
//			.stream()
//			.map(ContextStack::dump)
//			.toList();
//	}

	private final Deque<StackItem> stack;
	private final Thread owner;
	@Nullable
	private volatile Context localGlobalContext;

	@CallerSensitive
	private ContextStack() {
		this.stack = new LinkedList<>();
		this.owner = Thread.currentThread();
		this.localGlobalContext = globalContext;

		INSTANCE_MAP.put(this.owner, this);
	}

	private void updateGlobal() {
		this.localGlobalContext = globalContext;
	}

	@ThreadSensitive
	private void checkThread(@NotNull String methodName) {
		Objects.requireNonNull(methodName, "methodName is null");

		Thread currentThread = Thread.currentThread();
		if (currentThread != this.owner)
			throw new IllegalCallerException("Cannot invoke " + methodName + " in thread " + currentThread + ", owner thread is " + this.owner);
	}

	public void pushHidden() {
		this.checkThread("pushHidden");

		StackItem parentItem = this.stack.peek();

		boolean parentDebug = parentItem != null && parentItem.isDebug();

		StackItem item = new HiddenStackItem(this.owner, parentDebug);
		this.stack.push(item);

		if (item.isEffectiveDebug())
			LOGGER.trace("Push hidden layer in thread {}", this.owner);
	}

	@ThreadSensitive
	public void push(@Nullable Context ctx) {
		this.checkThread("push");

		StackItem parentItem = this.stack.peek();

		boolean parentDebug = parentItem != null && parentItem.isDebug();
		boolean debug = ctx != null && ctx.isDebug();

		StackItem item = new ContextStackItem(
			ctx,
			this.owner,
			debug,
			parentDebug
		);

		this.stack.push(item);

		if (item.isEffectiveDebug())
			LOGGER.trace("Push context in thread {}: {}", this.owner, lazy(ctx));
	}

	public void pop(@Nullable Context ctx) {
		this.checkThread("pop");

		StackItem item = this.stack.peek();
		if (item == null)
			throw new IllegalStateException("Context stack is empty");

		if (item instanceof HiddenStackItem)
			throw new IllegalStateException("Stack item is a hidden layer, not a context");

		Context itemContext = item.getContext();
		if (itemContext != ctx)
			throw new IllegalArgumentException("Context in stack " + itemContext + " is not a context " + ctx);

		this.stack.pop();

		if (item.isEffectiveDebug())
			LOGGER.trace("Pop context in thread {}: {}", this.owner, itemContext);
	}

	public void popHidden() {
		this.checkThread("popHidden");

		StackItem item = this.stack.peek();
		if (item == null)
			throw new IllegalStateException("Context stack is empty");

		if (!(item instanceof HiddenStackItem))
			throw new IllegalStateException("Stack item is a context, not a hidden layer");

		this.stack.pop();

		if (item.isEffectiveDebug())
			LOGGER.trace("Pop hidden layer in thread {}", this.owner);
	}

	@Nullable
	public Context getCurrent() {
		StackItem item = this.getCurrentItem();
		return item == null ? this.localGlobalContext : item.getContext();
	}

	@Nullable
	protected StackItem getCurrentItem() {
		return this.stack.peek();
	}

	public boolean isDebug() {
		StackItem item = this.getCurrentItem();
		return item != null && item.isDebug();
	}

	public boolean isEffectiveDebug() {
		StackItem item = this.getCurrentItem();
		Context localGlobalContext = this.localGlobalContext;

		return (item != null && item.isEffectiveDebug())
			|| (localGlobalContext != null && localGlobalContext.isDebug());
	}

	@NotNull
	@Unmodifiable
	public Stream<Context> getStackSnapshot() {
		boolean[] foundHiddenItem = new boolean[]{false};

		List<Context> snapshot = List
			.copyOf(this.stack)
			.stream()
			.filter(i -> {
				if (i instanceof HiddenStackItem) {
					foundHiddenItem[0] = true;
					return false;
				}

				return !foundHiddenItem[0];
			})
			.map(StackItem::getContext)
			.collect(Collectors.toList());

		snapshot.add(this.localGlobalContext);

		return snapshot
			.stream()
			.filter(Objects::nonNull);
	}

//	@NotNull
//	public ContextDumpInfo dump() {
//		long dumpTimeNanos = System.nanoTime();
//		ThreadSnapshot threadSnapshot = ThreadSnapshot.snapshot(this.owner);
//
//		List<StackItemDumpInfo> itemDumpInfos = List
//			.copyOf(this.stack)
//			.stream()
//			.map(StackItem::dump)
//			.toList();
//	}

	public int size() {
		return this.stack.size();
	}

	protected static abstract class StackItem {
		private final Thread owner;

		private final boolean debug;
		private final boolean parentDebug;
		@Nullable
		private final ThreadSnapshot pushThreadSnapshot;

		public StackItem(@NotNull Thread owner, boolean debug, boolean parentDebug) {
			this.owner = owner;

			this.debug = debug;
			this.parentDebug = parentDebug;

			this.pushThreadSnapshot = this.isEffectiveDebug()
				? ThreadSnapshot.snapshot(owner)
				: null;
		}

		@Nullable
		public abstract Context getContext();

		@NotNull
		public Thread getOwner() {
			return this.owner;
		}

		public boolean isDebug() {
			return this.debug;
		}

		public boolean isEffectiveDebug() {
			return this.debug || this.parentDebug;
		}

//		@Nullable
//		public StackItemDumpInfo dump() {
//			return new StackItemDumpInfo(StackItemDumpInfo.Type.CONTEXT, );
//		}
	}

	protected static class ContextStackItem extends StackItem {
		private final Context context;

		protected ContextStackItem(@Nullable Context context, @NotNull Thread owner, boolean debug, boolean parentDebug) {
			super(owner, debug, parentDebug);
			this.context = context;
		}

		@Nullable
		@Override
		public Context getContext() {
			return this.context;
		}
	}

	protected static class HiddenStackItem extends StackItem {
		protected HiddenStackItem(@NotNull Thread owner, boolean parentDebug) {
			super(owner, false, parentDebug);
		}

		@Nullable
		@Override
		public Context getContext() {
			return null;
		}
	}
}
