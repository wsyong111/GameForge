package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.framework.lifecycle.ILifecycle;
import io.github.wsyong11.gameforge.framework.lifecycle.Lifecycle;
import io.github.wsyong11.gameforge.framework.lifecycle.LifecycleProvider;
import io.github.wsyong11.gameforge.framework.lifecycle.LifecycleState;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.render.RenderSystem;
import io.github.wsyong11.gameforge.framework.system.render.ex.RenderSystemInitiationException;
import io.github.wsyong11.gameforge.framework.system.render.impl.opengl.OpenGL330RenderEngine;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import io.github.wsyong11.gameforge.framework.system.window.Window;
import io.github.wsyong11.gameforge.framework.system.window.impl.glfw.GLFWWindowManager;
import io.github.wsyong11.gameforge.util.concurrent.signal.ThreadSignal;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.Objects;

public class RenderThread extends Thread implements AutoCloseable, LifecycleProvider {
	private static final Logger LOGGER = Log.getLogger();

	private final ResourceProvider resourceProvider;
	private final boolean debug;

	private final Lifecycle lifecycle;

	private final ThreadSignal initedSignal;
	private final ThreadSignal runSignal;

	private volatile RenderSystem renderSystem;
	private volatile RenderSystemInitiationException initException;

	public RenderThread(@NotNull ResourceProvider resourceProvider, boolean debug) {
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");

		this.resourceProvider = resourceProvider;
		this.debug = debug;

		this.lifecycle = Lifecycle.debug(Lifecycle.create(), "RenderThread");

		this.initedSignal = new ThreadSignal();
		this.runSignal = new ThreadSignal();

		this.renderSystem = null;
		this.initException = null;

		this.setName("RenderThread");
	}

	@NotNull
	public RenderSystem getRenderSystem() {
		if (this.renderSystem == null)
			throw new IllegalStateException("Render thread is not running");

		return this.renderSystem;
	}

	@NotNull
	public Window getWindow() {
		return this.getRenderSystem().getWindow();
	}

	@Override
	public void run() {
		LOGGER.debug("Initialization render system, debug = {}", this.debug);

		this.lifecycle.setState(LifecycleState.STARTING);
		try {
			this.renderSystem = RenderSystem.init(
				this.resourceProvider,
				new Vector2i(800, 600),
				this.debug,
				() -> OpenGL330RenderEngine::new,
				() -> GLFWWindowManager::new
			);
		} catch (RenderSystemInitiationException e) {
			LOGGER.error("Render system initialization failed", e);
			this.lifecycle.setState(LifecycleState.ERROR);

			this.initException = e;
			this.initedSignal.set();
			return;
		}

		this.initedSignal.set();

		Window window = this.renderSystem.getWindow();
		window.setTitle("Game");

		try {
			this.runSignal.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.lifecycle.setState(LifecycleState.RUNNING);

		LOGGER.debug("Start rendering loop");
		try {
			window.setVisible(true);
			RenderSystem.loop();
		} catch (Exception e) {
			LOGGER.error("An exception occurred in the rendering loop", e);
			this.lifecycle.setState(LifecycleState.ERROR);
			return;
		}

		this.lifecycle.setState(LifecycleState.STOPPING);
		RenderSystem.shutdown();
		this.renderSystem = null;
		this.lifecycle.setState(LifecycleState.DESTROYED);

		LOGGER.debug("Render system stopped");
	}

	public void init() throws InterruptedException, RenderSystemInitiationException {
		this.start();

		this.initedSignal.await();
		if (this.initException != null)
			throw this.initException;
	}

	public void runThread() {
		this.runSignal.set();
	}

	@Override
	public void close() {
		if (this.renderSystem == null)
			return;

		LOGGER.debug("Stopping render system");
		this.renderSystem.runOnUIThread(RenderSystem::shutdown);
	}

	@NotNull
	@Override
	public ILifecycle getLifecycle() {
		return this.lifecycle;
	}
}
