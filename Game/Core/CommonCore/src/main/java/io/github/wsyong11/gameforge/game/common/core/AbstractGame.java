package io.github.wsyong11.gameforge.game.common.core;

import io.github.wsyong11.gameforge.game.common.core.StartupConfig;
import io.github.wsyong11.gameforge.framework.app.Application;
import io.github.wsyong11.gameforge.game.common.Game;
import io.github.wsyong11.gameforge.game.common.GameContext;
import io.github.wsyong11.gameforge.game.common.GameEnvConfig;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractGame extends Application implements Game {
	private final StartupConfig config;

	public AbstractGame(@NotNull StartupConfig config) {
		Objects.requireNonNull(config, "config is null");
		this.config = config;
	}

	// TODO: 2025/9/2 Impl game context
	@Override
	public @Nullable GameContext getContext() {
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
		super.onDestroy();
	}

	@Override
	protected void onError(@NotNull Throwable exception) {
		super.onError(exception);
	}

	@NotNull
	@Override
	public GameEnvConfig getEnvConfig() {
		return this.config;
	}
}
