package io.github.wsyong11.gameforge.framework.system.render;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngine;
import io.github.wsyong11.gameforge.framework.system.render.listener.LogicSizeListener;
import io.github.wsyong11.gameforge.framework.system.render.listener.RenderEngineErrorListener;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Objects;

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
		@NotNull RenderEngineProvider provider
	) {
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");
		Objects.requireNonNull(logicSize, "logicSize is null");
		Objects.requireNonNull(provider, "provider is null");

		if (INSTANCE.get() != null)
			throw new IllegalThreadStateException("This thread is bound to RenderSystem");

		Thread thread = Thread.currentThread();

		LOGGER.debug("RenderSystem bound to thread {}", thread);

		RenderSystem renderSystem = new RenderSystem(resourceProvider, provider, logicSize, debug);

		INSTANCE.set(renderSystem);
		return renderSystem;
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

	private final RenderEngine engine;
	private final RenderEngineLifecycle renderEngineLifecycle;

	private RenderSystem(
		@NotNull ResourceProvider resourceProvider,
		@NotNull RenderEngineProvider provider,
		@NotNull Vector2ic logicSize,
		boolean debug
	) {
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");
		Objects.requireNonNull(provider, "provider is null");
		Objects.requireNonNull(logicSize, "logicSize is null");

		this.logicSize = new Vector2i(logicSize);
		this.debug = debug;

		this.listenerList = ListenerList.sync();

		this.renderEngineLifecycle = new RenderEngineLifecycle(this.listenerList);

		this.engine = provider.getFactory().apply(this.renderEngineLifecycle);
	}

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

	private void shutdownThis() {
		this.listenerList.clear();
	}

	private static class RenderEngineLifecycle implements RenderEngine.Lifecycle {
		private final ListenerList listenerList;

		public RenderEngineLifecycle(@NotNull ListenerList listenerList) {
			Objects.requireNonNull(listenerList, "listenerList is null");
			this.listenerList = listenerList;
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
	}
}
