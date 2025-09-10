package io.github.wsyong11.gameforge.game.common.core;

import io.github.wsyong11.gameforge.framework.app.Application;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.pack.AssetsResourcePack;
import io.github.wsyong11.gameforge.game.common.Game;
import io.github.wsyong11.gameforge.game.common.GameContext;
import io.github.wsyong11.gameforge.game.common.GameEnvConfig;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 游戏的基本抽象实现，实现了基本的资源管理系统
 */
public abstract class AbstractGame extends Application implements Game {
	private static final Logger LOGGER = Log.getLogger();

	private final StartupConfig config;
	private final DefaultResourceManager resourceManager;

	private volatile boolean cleaned;

	/**
	 * 实例化对象
	 *
	 * @param config           启动配置
	 * @param resourceBasePath 基础资源路径
	 */
	public AbstractGame(@NotNull StartupConfig config, @NotNull ResourcePath resourceBasePath) {
		Objects.requireNonNull(config, "config is null");
		Objects.requireNonNull(resourceBasePath, "resourceBasePath is null");

		this.config = config;

		this.resourceManager = new DefaultResourceManager(resourceBasePath);

		this.cleaned = false;
	}

	/**
	 * 获取资源管理器实例
	 *
	 * @return 资源管理器实例
	 */
	@NotNull
	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	// TODO: 2025/9/2 Impl game context
	@NotNull
	@Override
	public GameContext getContext() {
		return null;
	}

	/**
	 * 获取启动配置
	 *
	 * @return 当前游戏实例的启动配置对象
	 */
	@NotNull
	public StartupConfig getConfig() {
		return this.config;
	}

	/**
	 * 获取当前类加载器
	 *
	 * @return 将返回加载当前类的类加载器
	 */
	@NotNull
	public ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}

	@MustBeInvokedByOverriders
	@Override
	protected void onStarting() throws Throwable {
		super.onStarting();

		LOGGER.debug("Setting up the resource system");
		this.resourceManager.addPack(new AssetsResourcePack("game"));
	}

	@MustBeInvokedByOverriders
	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();
		this.resourceManager.reload();
	}

	@MustBeInvokedByOverriders
	@Override
	protected void onStopping() throws Throwable {
		super.onStopping();
	}

	@MustBeInvokedByOverriders
	@Override
	protected void onDestroy() {
		try {
			super.onDestroy();
		} finally {
			this.clean();
		}
	}

	@Override
	protected void onError(@NotNull Throwable exception) {
		try {
			super.onError(exception);
		} finally {
			this.clean();
		}
	}

	/**
	 * 退出前的清理函数，将在 {@link #onDestroy()} 或 {@link #onError(Throwable)} 后调用
	 *
	 * @implNote 应始终假定初始化不完整，以避免访问字段时的空指针异常
	 */
	@MustBeInvokedByOverriders
	protected void clean() {
		if (this.cleaned)
			return;
		this.cleaned = true;

		ExceptionHandler handler = new ExceptionHandler();

		LOGGER.info("Cleaning resources");
		handler.run(this.resourceManager::clean);

		if (!handler.isEmpty()) {
			RuntimeException exception = handler.toException(RuntimeException::new);
			exception.fillInStackTrace();
			LOGGER.warn("An exception occurred during cleaning", exception);
		}
	}

	@NotNull
	@Override
	public GameEnvConfig getEnvConfig() {
		return this.config;
	}
}
