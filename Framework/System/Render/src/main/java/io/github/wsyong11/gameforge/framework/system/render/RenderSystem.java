package io.github.wsyong11.gameforge.framework.system.render;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngine;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngineContext;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderSystemContext;
import io.github.wsyong11.gameforge.framework.system.render.listener.LogicSizeListener;
import io.github.wsyong11.gameforge.framework.system.render.listener.RenderEngineErrorListener;
import io.github.wsyong11.gameforge.framework.system.render.provider.RenderEngineProvider;
import io.github.wsyong11.gameforge.framework.system.render.provider.WindowManagerProvider;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.framework.system.window.WindowManager;
import io.github.wsyong11.gameforge.util.concurrent.TaskHandler;
import io.github.wsyong11.gameforge.util.concurrent.executor.TaskQueueExecutor;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.lang.invoke.LambdaMetafactory;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public final class RenderSystem {
	private static final Logger LOGGER = Log.getLogger();

	private static final ThreadLocal<RenderSystem> INSTANCE = new ThreadLocal<>();

	@NotNull
	public static RenderSystem init(
		@NotNull ResourceProvider resourceProvider,
		@NotNull Vector2ic logicSize,
		boolean debug,
		@NotNull RenderEngineProvider engineProvider,
		@NotNull WindowManagerProvider windowManagerProvider,
		) {
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");
		Objects.requireNonNull(logicSize, "logicSize is null");
		Objects.requireNonNull(engineProvider, "engineProvider is null");

		if (INSTANCE.get() != null)
			throw new IllegalThreadStateException("This thread is bound to RenderSystem");

		Thread thread = Thread.currentThread();

		LOGGER.debug("RenderSystem bound to thread {}", thread);

		RenderSystem renderSystem = new RenderSystem(resourceProvider, engineProvider, windowManagerProvider, logicSize, debug);

		INSTANCE.set(renderSystem);
		return renderSystem;
	}

	public static void loop() {
		RenderSystem instance = getInstance();
		if (instance.isRunning())
			throw new IllegalStateException("Render system for this thread is running");
		instance.loopThis();
	}

	public static void shutdown() {
		RenderSystem instance = getInstanceSafe();
		if (instance == null) {
			LOGGER.warn("Attempted to shutdown RenderSystem, but none was bound");
			return;
		}

		LOGGER.debug("Shutdown RenderSystem on thread {}", Thread.currentThread());
		instance.shutdownThis();
		INSTANCE.remove();
	}

	@Nullable
	public static RenderSystem getInstanceSafe() {
		return INSTANCE.get();
	}

	@NotNull
	public static RenderSystem getInstance() {
		RenderSystem instance = getInstanceSafe();
		if (instance == null)
			throw new IllegalThreadStateException("This thread is not bind to RenderSystem");
		return instance;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final boolean debug;
	private final Vector2i logicSize;

	private final ListenerList listenerList;

	private final TaskQueueExecutor taskExecutor;
	private final TaskHandler taskHandler;

	private final WindowManager windowManager;
	private final RenderEngine engine;
	private final Context context;

	private boolean running;

	private RenderSystem(
		@NotNull ResourceProvider resourceProvider,
		@NotNull RenderEngineProvider provider,
		@NotNull WindowManagerProvider windowManagerProvider,
		@NotNull Vector2ic logicSize,
		boolean debug
	) {
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");
		Objects.requireNonNull(provider, "provider is null");
		Objects.requireNonNull(logicSize, "logicSize is null");

		this.logicSize = new Vector2i(logicSize);
		this.debug = debug;

		this.listenerList = ListenerList.sync();

		this.taskExecutor = new TaskQueueExecutor();
		this.taskHandler = new TaskHandler(this.taskExecutor);

		this.context = new Context(this.listenerList, this.taskHandler);

		this.running = false;

		this.windowManager = windowManagerProvider.getFactory().get();
		this.engine = provider.getFactory().apply(this.context);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public boolean isDebug() {
		return this.debug;
	}

	@NotNull
	public Vector2ic getLogicSize() {
		return this.logicSize;
	}

	public void setLogicSize(@NotNull Vector2ic size) {
		Objects.requireNonNull(size, "size is null");

		if (Objects.equals(this.logicSize, size))
			return;
		this.logicSize.set(size);

		this.listenerList.fire(
			LogicSizeListener.class,
			l -> l.onLogicSizeChanged(this.logicSize),
			ListenerExceptionCallback.log(LOGGER));
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public TaskHandler getTaskHandler() {
		return this.taskHandler;
	}

	public boolean isRunning() {
		return this.running;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void executeTask() {
		this.taskExecutor.run(exception ->
			LOGGER.error("An exception occurred during running a task", exception));
	}

	private void loopThis() {
		if (this.running)
			return;
		this.running = true;

		while (this.running) {
			this.executeTask();
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void shutdownThis() {
		this.running = false;

		this.listenerList.clear();

		if (!this.taskExecutor.isEmpty())
			LOGGER.warn("Task queue is not empty");

		this.taskExecutor.clear();

		ExceptionHandler
			.create()
			.run(this.engine::close)
			.pickOnce(e -> LOGGER.warn("An exception occurred while closing the render engine", e))
			.run(this.windowManager::close)
			.pickOnce(e -> LOGGER.warn("An exception occurred while closing the window manager", e));
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private static class Context implements RenderSystemContext, RenderEngineContext {
		private final ListenerList listenerList;
		private final TaskHandler taskHandler;

		public Context(@NotNull ListenerList listenerList, TaskHandler taskHandler) {
			Objects.requireNonNull(listenerList, "listenerList is null");
			Objects.requireNonNull(taskHandler, "taskHandler is null");

			this.listenerList = listenerList;
			this.taskHandler = taskHandler;
		}

		@Override
		public void error(@NotNull LogLevel level, @NotNull String message, int code) {
			Objects.requireNonNull(level, "level is null");
			Objects.requireNonNull(message, "message is null");

			this.listenerList.fire(
				RenderEngineErrorListener.class,
				l -> l.onRenderEngineError(level, message, code),
				ListenerExceptionCallback.log(LOGGER));
		}

		@Override
		public void registerLogicSizeListener(@NotNull LogicSizeListener listener) {
			Objects.requireNonNull(listener, "listener is null");
			this.listenerList.add(LogicSizeListener.class, listener);
		}

		@Override
		public void unregisterLogicSizeListener(@NotNull LogicSizeListener listener) {
			Objects.requireNonNull(listener, "listener is null");
			this.listenerList.remove(LogicSizeListener.class, listener);
		}

		@NotNull
		@Override
		public TaskHandler getTaskHandler() {
			return this.taskHandler;
		}
	}
}
