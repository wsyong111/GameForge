package io.github.wsyong11.gameforge.game.common.core;

import io.github.wsyong11.gameforge.framework.app.Application;
import io.github.wsyong11.gameforge.framework.app.BootstrapConfig;
import io.github.wsyong11.gameforge.framework.app.BootstrapContext;
import io.github.wsyong11.gameforge.framework.event.EventBus;
import io.github.wsyong11.gameforge.framework.event.manager.EventBusManager;
import io.github.wsyong11.gameforge.framework.event.manager.SimpleEventBusManager;
import io.github.wsyong11.gameforge.framework.lifecycle.ILifecycle;
import io.github.wsyong11.gameforge.framework.lifecycle.Lifecycle;
import io.github.wsyong11.gameforge.framework.lifecycle.LifecycleState;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.tick.TickInfo;
import io.github.wsyong11.gameforge.framework.tick.TickManager;
import io.github.wsyong11.gameforge.game.common.Game;
import io.github.wsyong11.gameforge.game.common.GameContext;
import io.github.wsyong11.gameforge.game.common.GameEnvConfig;
import io.github.wsyong11.gameforge.game.common.core.service.EventBusServiceStub;
import io.github.wsyong11.gameforge.game.common.core.service.ResourceManagerServiceStub;
import io.github.wsyong11.gameforge.game.common.core.service.ServiceRegistryImpl;
import io.github.wsyong11.gameforge.game.common.core.service.TickServiceStub;
import io.github.wsyong11.gameforge.game.common.core.tick.RootTickManager;
import io.github.wsyong11.gameforge.game.common.event.StopEvent;
import io.github.wsyong11.gameforge.game.common.service.EventBusService;
import io.github.wsyong11.gameforge.game.common.service.ResourceManagerService;
import io.github.wsyong11.gameforge.game.common.service.ServiceRegistry;
import io.github.wsyong11.gameforge.game.common.service.TickService;
import io.github.wsyong11.gameforge.util.exception.ExceptionRunnable;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

/**
 * 游戏的基本抽象实现，实现了基本的资源管理系统
 */
public abstract class AbstractGame extends Application implements Game {
	private static final Logger LOGGER = Log.getLogger();

	public static final BootstrapConfig<Path> CONFIG_TEMP_DIR = BootstrapConfig.create("tempDir", Path.class);
	public static final BootstrapConfig<Path> CONFIG_MOD_DIR = BootstrapConfig.create("modDir", Path.class);
	public static final BootstrapConfig<Path> CONFIG_CONFIG_DIR = BootstrapConfig.create("configDir", Path.class);
	public static final BootstrapConfig<Path> CONFIG_CRASH_REPORT_DIR = BootstrapConfig.create("crashReportDir", Path.class);
	public static final BootstrapConfig<List<Path>> CONFIG_MOD_JAR_PATHS = BootstrapConfig.create("modJarPaths", List.class);
	public static final BootstrapConfig<Boolean> CONFIG_SAFE_MODE = BootstrapConfig.create("safeMode", Boolean.class);

	private final StartupConfig startupConfig;

	private final Lifecycle lifecycle;

	private final EventBusManager eventBusManager;
	private final EventBus systemEventBus;

	private final ServiceRegistry serviceRegistry;

	private final TickManager tickManager;
	private final ResourceManager resourceManager;

	private final GameLoop gameLoop;

	private volatile GameContext context;

	/**
	 * 实例化对象
	 *
	 * @param bootstrapContext 启动信息
	 * @param resourceBasePath 基础资源路径
	 */
	public AbstractGame(@NotNull BootstrapContext bootstrapContext, @NotNull ResourcePath resourceBasePath) {
		Objects.requireNonNull(bootstrapContext, "bootstrapContext is null");
		Objects.requireNonNull(resourceBasePath, "resourceBasePath is null");

		this.startupConfig = new StartupConfig(
			bootstrapContext.getLogDir(),
			bootstrapContext.getConfigRequire(CONFIG_TEMP_DIR),
			bootstrapContext.getConfigRequire(CONFIG_MOD_DIR),
			bootstrapContext.getConfigRequire(CONFIG_CONFIG_DIR),
			bootstrapContext.getConfigRequire(CONFIG_CRASH_REPORT_DIR),
			bootstrapContext.isDebug(),
			bootstrapContext.getConfigRequire(CONFIG_MOD_JAR_PATHS),
			bootstrapContext.getConfigRequire(CONFIG_SAFE_MODE)
		);

		this.lifecycle = Lifecycle.debug(Lifecycle.create(), this.getClass().getSimpleName());

		this.serviceRegistry = new ServiceRegistryImpl();

		this.eventBusManager = new SimpleEventBusManager();
		this.systemEventBus = EventBus.simple();

		this.tickManager = new RootTickManager();
		this.resourceManager = new DefaultResourceManager(resourceBasePath);

		this.gameLoop = new GameLoop(DEFAULT_TPS, this.tickManager);

		this.context = null;
	}

	@NotNull
	@Override
	public GameEnvConfig getEnvConfig() {
		return this.startupConfig;
	}

	@NotNull
	@Override
	public ILifecycle getLifecycle() {
		return this.lifecycle;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	protected ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	@NotNull
	protected EventBusManager getEventBusManager() {
		return this.eventBusManager;
	}

	@NotNull
	protected TickManager getTickManager() {
		return this.tickManager;
	}

	@NotNull
	protected EventBus getSystemEventBus() {
		return this.systemEventBus;
	}

	@NotNull
	protected ServiceRegistry getServiceRegistry() {
		return this.serviceRegistry;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void requireStop(){
		this.lifecycle.assertState(LifecycleState.RUNNING);
		this.gameLoop.stop();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void onPreStarting() throws Throwable {
	}

	protected void onStarting() throws Throwable {
		this.onPreStarting();

		// 注册自己以便允许外部通过 getService 获取服务注册表
		this.serviceRegistry.register(ServiceRegistry.class, this.serviceRegistry);

		this.serviceRegistry.register(EventBusService.class, new EventBusServiceStub(this.eventBusManager));
		this.serviceRegistry.register(TickService.class, new TickServiceStub(this.tickManager));
		this.serviceRegistry.register(ResourceManagerService.class, new ResourceManagerServiceStub(this.resourceManager));

		this.eventBusManager.registerEventBus(EventBusService.SYSTEM, this.systemEventBus);

		this.tickManager.buildTask(this::tick)
		                .priority(Integer.MAX_VALUE)
		                .build();

		this.onPostStarting();
	}

	protected void onPostStarting() throws Throwable {
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void onPreRunning() throws Throwable {
	}

	protected void onRunning() throws Throwable {
		this.onPreRunning();

		LOGGER.debug("Create game context");
		this.context = this.createGameContext(this.serviceRegistry);
		if (this.context == null)
			throw new IllegalStateException("Failed to create game context");
		LOGGER.debug("Current game context: {}", lazy(this.context));

		this.resourceManager.reload();

		this.onPostRunning();
	}

	protected void onPostRunning() throws Throwable {
		this.gameLoop.run();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void onPreStopping() throws Throwable {
	}

	protected void onStopping() throws Throwable {
		this.onPreStopping();

		this.systemEventBus.postAsync(new StopEvent(StopEvent.Type.NORMAL));

		this.onPostStopping();
	}

	protected void onPostStopping() throws Throwable {
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void onPreDestroyed() throws Throwable {
	}

	protected void onDestroyed() throws Throwable {
		this.onPreDestroyed();

		this.resourceManager.close();
		this.eventBusManager.close();
		this.context = null;

		this.onPostDestroyed();
	}

	protected void onPostDestroyed() throws Throwable {
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	protected abstract GameContext createGameContext(@NotNull ServiceRegistry serviceRegistry);

	@NotNull
	@Override
	public GameContext getContext() {
		this.lifecycle.assertState(LifecycleState.RUNNING, LifecycleState.STOPPING);

		GameContext context = this.context;

		if (context == null)
			throw new IllegalStateException("Game context doesn't create");
		return context;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void tick(@NotNull TickInfo info) {
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void processError(@NotNull Throwable exception) throws Throwable {
		Objects.requireNonNull(exception, "exception is null");
		LOGGER.error("Runtime uncaught exception", exception);
		throw exception;
	}

	private boolean lifecycleChange(@NotNull LifecycleState oldState, @NotNull LifecycleState newState, @NotNull ExceptionRunnable<Throwable> runnable) throws Throwable {
		Objects.requireNonNull(oldState, "oldState is null");
		Objects.requireNonNull(newState, "newState is null");
		Objects.requireNonNull(runnable, "runnable is null");

		this.lifecycle.assertState(oldState);
		try {
			this.lifecycle.setState(newState);
			runnable.run();
			return false;
		} catch (Throwable e) {
			try {
				this.processError(e);
				return true;
			} finally {
				this.lifecycle.setState(LifecycleState.ERROR);
			}
		}
	}

	@Override
	public int main() throws Throwable {
		if (this.lifecycleChange(LifecycleState.CREATED, LifecycleState.STARTING, this::onStarting)) return 1;
		if (this.lifecycleChange(LifecycleState.STARTING, LifecycleState.RUNNING, this::onRunning)) return 1;
		if (this.lifecycleChange(LifecycleState.RUNNING, LifecycleState.STOPPING, this::onStopping)) return 1;
		if (this.lifecycleChange(LifecycleState.STOPPING, LifecycleState.DESTROYED, this::onDestroyed)) return 1;
		return 0;
	}

	protected boolean isDebug() {
		return this.startupConfig.isDebug();
	}
}
