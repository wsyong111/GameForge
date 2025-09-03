package io.github.wsyong11.gameforge.framework.app;

import io.github.wsyong11.gameforge.framework.lifecycle.LifecycleInstance;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

public abstract class Application extends LifecycleInstance {
	private static final Logger LOGGER = Log.getLogger();

	public Application() {
		super("App");
	}

	@Override
	protected void onStarting() throws Throwable {

	}

	@Override
	protected void onRunning() throws Throwable {

	}

	@Override
	protected void onStopping() throws Throwable {

	}

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onError(@NotNull Throwable exception) {
		LOGGER.error("Uncaught exception occurred", exception);
	}

	@Override
	public String toString() {
		return "Application[" + this.lifecycle + "]";
	}
}
