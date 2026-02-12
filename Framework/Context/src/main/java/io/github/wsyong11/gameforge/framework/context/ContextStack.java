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
		this.stack = new ArrayDeque<>();
		this.owner = Thread.currentThread();
		this.localGlobalContext = globalContext;

		INSTANCE_MAP.put(this.owner, this);
	}

	private void updateGlobal() {
		this.localGlobalContext = globalContext;
	}

	@NotNull
	public Thread getOwner() {
		return this.owner;
	}

	@ThreadSensitive
	private void checkThread(@NotNull String methodName) {
		Objects.requireNonNull(methodName, "methodName is null");

		Thread currentThread = Thread.currentThread();
		if (currentThread != this.owner)
			throw new IllegalCallerException("Cannot invoke " + methodName + " in thread " + currentThread + ", owner thread is " + this.owner);
	}

	@ThreadSensitive
	protected void push(@NotNull StackItemType type, @Nullable Context ctx) {
		Objects.requireNonNull(type, "type is null");

		if (type == StackItemType.DISABLE && ctx != null)
			throw new IllegalArgumentException("Stack item type is DISABLE, but ctx parameter is not null");

		this.checkThread("push");

		StackItem parentItem = this.stack.peek();

		boolean effectiveDebug = parentItem != null && parentItem.isEffectiveDebug();
		boolean debug = ctx != null && ctx.isDebug();

		StackItem item = new StackItem(
			ctx,
			type,
			this.owner,
			debug,
			effectiveDebug
		);

		this.stack.push(item);

		if (item.isEffectiveDebug())
			LOGGER.trace("Push stack {} in thread {}: {}", type, this.owner, lazy(ctx));
	}

	public void pop(@NotNull StackItemType type, @Nullable Context ctx) {
		Objects.requireNonNull(type, "type is null");

		this.checkThread("pop");

		StackItem item = this.stack.peek();
		if (item == null)
			throw new IllegalStateException("Context stack is empty");

		StackItemType itemType = item.getType();
		if (itemType != type)
			throw new IllegalStateException("Stack item is a " + itemType + ", but type parameter is " + type);

		Context itemContext = item.getContext();
		if (itemContext != ctx)
			throw new IllegalArgumentException("Context in stack " + itemContext + " is not a context " + ctx);

		this.stack.pop();

		if (item.isEffectiveDebug())
			LOGGER.trace("Pop stack {} in thread {}: {}", itemType, this.owner, itemContext);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@ThreadSensitive
	public void pushHidden(@Nullable Context ctx) {
		this.checkThread("pushHidden");
		this.push(StackItemType.HIDDEN, ctx);
	}

	@ThreadSensitive
	public void push(@NotNull Context ctx) {
		Objects.requireNonNull(ctx, "ctx is null");
		this.checkThread("push");
		this.push(StackItemType.CONTEXT, ctx);
	}

	@ThreadSensitive
	public void pushDisable() {
		this.checkThread("pushDisable");
		this.push(StackItemType.DISABLE, null);
	}

	@ThreadSensitive
	public void pop(@NotNull Context ctx) {
		Objects.requireNonNull(ctx, "ctx is null");
		this.checkThread("pop");
		this.pop(StackItemType.CONTEXT, ctx);
	}

	@ThreadSensitive
	public void popHidden(@Nullable Context ctx) {
		this.checkThread("popHidden");
		this.pop(StackItemType.HIDDEN, ctx);
	}

	@ThreadSensitive
	public void popDisable() {
		this.checkThread("popDisable");
		this.pop(StackItemType.DISABLE, null);
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
				if (i.getType()==StackItemType.HIDDEN) {
					foundHiddenItem[0] = true;
					return true;
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

	protected static class StackItem {
		@Nullable
		private final Context context;
		private final Thread owner;
		private final StackItemType type;

		private final boolean debug;
		private final boolean effectiveDebug;

		@Nullable
		private final ThreadSnapshot pushThreadSnapshot;

		public StackItem(@Nullable Context ctx, @NotNull StackItemType type, @NotNull Thread owner, boolean debug, boolean effectiveDebug) {
			Objects.requireNonNull(owner, "owner is null");
			Objects.requireNonNull(type, "type is null");

			this.context = ctx;
			this.owner = owner;
			this.type = type;

			if (type == StackItemType.DISABLE && ctx != null)
				throw new IllegalArgumentException("Stack item type is DISABLE, but ctx parameter is not null");

			this.debug = debug;
			this.effectiveDebug = effectiveDebug;

			this.pushThreadSnapshot = this.isEffectiveDebug()
				? ThreadSnapshot.snapshot(owner)
				: null;
		}

		@Nullable
		public Context getContext() {
			return this.context;
		}

		@NotNull
		public Thread getOwner() {
			return this.owner;
		}

		@NotNull
		public StackItemType getType() {
			return this.type;
		}

		public boolean isDebug() {
			return this.debug;
		}

		public boolean isEffectiveDebug() {
			return this.debug || this.effectiveDebug;
		}

//		@Nullable
//		public StackItemDumpInfo dump() {
//			return new StackItemDumpInfo(StackItemDumpInfo.Type.CONTEXT, );
//		}
	}

	protected enum StackItemType {
		CONTEXT,
		DISABLE,
		HIDDEN
	}
}
