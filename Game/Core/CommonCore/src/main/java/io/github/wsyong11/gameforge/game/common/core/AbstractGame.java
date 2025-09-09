package io.github.wsyong11.gameforge.game.common.core;

import io.github.wsyong11.gameforge.framework.app.Application;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.game.common.Game;
import io.github.wsyong11.gameforge.game.common.GameContext;
import io.github.wsyong11.gameforge.game.common.GameEnvConfig;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractGame extends Application implements Game {
	private static final Logger LOGGER = Log.getLogger();

	private final StartupConfig config;
	private final DefaultResourceManager resourceManager;

	private volatile boolean cleaned;

	public AbstractGame(@NotNull StartupConfig config, @NotNull ResourcePath resourceBasePath) {
		Objects.requireNonNull(config, "config is null");
		Objects.requireNonNull(resourceBasePath, "resourceBasePath is null");

		this.config = config;

		this.resourceManager = new DefaultResourceManager(resourceBasePath);

		this.cleaned = false;
	}

	@NotNull
	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	// TODO: 2025/9/2 Impl game context
	@Nullable
	@Override
	public GameContext getContext() {
		return null;
	}

	@NotNull
	public StartupConfig getConfig() {
		return this.config;
	}

	@NotNull
	public ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}

	@MustBeInvokedByOverriders
	@Override
	protected void onStarting() throws Throwable {
		super.onStarting();
	}

	@MustBeInvokedByOverriders
	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();
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
