package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.TimeIt;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ReloadStatus;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.util.concurrent.FutureUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ResourceLoader {
	private static final Logger LOGGER = Log.getLogger();

	private final Thread thread;
	private boolean started;

	private final ListenableFuture<ResourceGraph> future;

	private final ReloadStatus status;

	protected ResourceLoader() {
		ListenableFutureTask<ResourceGraph> future = ListenableFutureTask.create(this::loadAsync);
		this.future = future;

		this.thread = new Thread(future);
		this.thread.setName("ResourceLoaderThread");
		this.thread.setDaemon(true);

		this.started = false;

		this.status = new StatusImpl();
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
		return this.status;
	}

	// TODO 2026/04/25: Impl status
	private class StatusImpl implements ReloadStatus {
		@NotNull
		@UnmodifiableView
		@Override
		public List<Stage> getStages() {
			return List.of();
		}

		@NotNull
		@Override
		public Stage getCurrentStage() {
			return null;
		}

		@Nullable
		@Override
		public Stage getStage(@NotNull String id) {
			return null;
		}

		@Override
		public boolean isDone() {
			return future.isDone();
		}

		@Override
		public boolean isSuccess() {
			return FutureUtils.isDoneNormal(future);
		}

		@Override
		public boolean isCancelled() {
			return future.isCancelled();
		}

		@Nullable
		@Override
		public Throwable getException() {
			return FutureUtils.getException(future);
		}

		@Override
		public void await() throws InterruptedException {
			try {
				future.get();
			} catch (ExecutionException ignored) {
			}
		}

		@Override
		public boolean await(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
			Objects.requireNonNull(unit, "unit is null");

			try {
				future.get(timeout, unit);
			} catch (ExecutionException ignored) {
			} catch (TimeoutException e) {
				return false;
			}

			return true;
		}

		@Override
		public void cancel() {
			future.cancel(true);
		}

		@Override
		public void addListener(@NotNull Listener listener) {

		}

		@Override
		public void removeListener(@NotNull Listener listener) {

		}
	}
}
