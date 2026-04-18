package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.TimeIt;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ReloadStatus;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CancellationException;

public abstract class ResourceLoader {
	private static final Logger LOGGER = Log.getLogger();

	private final Thread thread;
	private boolean started;

	private final ListenableFuture<ResourceGraph> future;

	protected ResourceLoader() {
		this.future = ListenableFutureTask.create(this::loadAsync);

		this.thread = new Thread(this::loadAsync);
		this.thread.setName("ResourceLoaderThread");
		this.thread.setDaemon(true);
	}

	@NotNull
	protected abstract ResourceGraph doLoad() throws InterruptedException;

	@NotNull
	private ResourceGraph loadAsync() {
		try (TimeIt ignored = TimeIt.begin(LOGGER, LogLevel.INFO, "Load resources")) {
			return this.doLoad();
		} catch (InterruptedException e) {
			LOGGER.info("Canceled load resources");
			throw new CancellationException();
		} catch (Throwable e) {
			LOGGER.error("Failed to load resources", e);
			throw e;
		}
	}

	public synchronized void load() {
		if (this.started)
			return;
		this.started = true;

		LOGGER.debug("Start background load thread");
		this.thread.start();
	}

	@NotNull
	public ListenableFuture<ResourceGraph> getFuture() {
		return this.future;
	}

	@NotNull
	public ReloadStatus getStatus() {
		return null;
	}
}
