package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.framework.spi.registry.ExtensionRegistry;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ReloadStatus;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceConflictResolver;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.util.concurrent.FutureThread;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractResourceManager implements ResourceManager {
	private final ResourcePackRegistry packRegistry;
	private final ExtensionRegistry extensions;

	@Nullable
	private volatile FutureThread<?> reloadThread;
	private volatile ReloadStatus lastReloadStatus;

	private volatile boolean closed;

	public AbstractResourceManager() {
		this.packRegistry = new ResourcePackRegistry();

		this.extensions = ExtensionRegistry.createRestrict(Set.of(
			ResourceConflictResolver.TYPE,
			ResourceTransformer.TYPE
		));

		this.reloadThread = null;
		this.lastReloadStatus = null;

		this.closed = false;
	}

	@NotNull
	protected ResourcePackRegistry getPackRegistry() {
		return this.packRegistry;
	}

	@NotNull
	protected ExtensionRegistry getExtensions() {
		return this.extensions;
	}

	protected void ensureOpen() {
		if (this.closed)
			throw new IllegalStateException("Resource manager closed");
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	protected abstract List<ReloadStatus.Stage> getReloadStages();

	@NotNull
	protected abstract ResourceReloader createReloader(
		@NotNull List<ResourcePack> packs,
		@NotNull List<ResourceConflictResolver> conflictResolvers,
		@NotNull List<ResourceTransformer> transformers
	);

	@NotNull
	@Override
	public synchronized ReloadStatus reload() {
		this.ensureOpen();


		this.packRegistry.update();

		List<ResourceConflictResolver> conflictResolvers = this.extensions.getExtensions(ResourceConflictResolver.TYPE);
		List<ResourceTransformer> transformers = this.extensions.getExtensions(ResourceTransformer.TYPE);

		List<ResourcePack> packs = this.packRegistry.getCurrentPacks();


		SimpleReloadStatus reloadStatus = new SimpleReloadStatus(this.getReloadStages());
		this.lastReloadStatus = reloadStatus;
		return reloadStatus;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@UnmodifiableView
	@Override
	public List<ResourcePack> getResourcePacks() {
		this.ensureOpen();
		return this.packRegistry.getPacks();
	}

	@Override
	public void setPackPriority(@NotNull ResourcePack pack, int priority) {
		Objects.requireNonNull(pack, "pack is null");

		this.ensureOpen();
		this.packRegistry.setPriority(pack, priority);
	}

	@Override
	public int getPackPriority(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		this.ensureOpen();
		return this.packRegistry.getPriority(pack);
	}

	@Override
	public void registerResourcePack(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		this.ensureOpen();
		this.packRegistry.register(pack);
	}

	@Override
	public void unregisterResourcePack(@NotNull ResourcePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		this.ensureOpen();
		this.packRegistry.unregister(pack);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public <T> void addExtension(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		this.ensureOpen();
		this.extensions.register(type, instance);
	}

	@Override
	public <T> void removeExtension(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		this.ensureOpen();
		this.extensions.unregister(type, instance);
	}

	@Override
	public <T> void removeExtension(@NotNull T instance) {
		Objects.requireNonNull(instance, "instance is null");

		this.ensureOpen();
		this.extensions.unregister(instance);
	}

	@Override
	public <T> void setExtensionPriority(@NotNull ExtensionType<T> type, @NotNull T instance, int priority) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		this.ensureOpen();
		this.extensions.setPriority(type, instance, priority);
	}

	@Override
	public <T> int getExtensionPriority(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		this.ensureOpen();
		return this.extensions.getPriority(type, instance);
	}

	@Override
	public <T> boolean hasExtension(@NotNull ExtensionType<T> type, @NotNull T instance) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(instance, "instance is null");

		this.ensureOpen();
		return this.extensions.has(instance);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void close() throws IOException {
		this.closed = true;

		ExceptionHandler handler = new ExceptionHandler();
		for (ResourcePack pack : this.packRegistry.getPacks())
			handler.close(pack);

		this.packRegistry.clearAll();
		this.extensions.clear();

		handler.throwException("An error occurred while closing", IOException::new);
	}

	//	protected static class SimpleReloadStatus implements ReloadStatus {
//		private final List<Stage> stages;
//		private volatile int stageIndex;
//
//		private volatile Future<?> future;
//
//		private final List<IdentityRef<Listener>> listeners;
//
//		protected SimpleReloadStatus(@NotNull List<Stage> stages) {
//			Objects.requireNonNull(stages, "stages is null");
//
//			this.stages = List.copyOf(stages);
//			this.future = null;
//			this.listeners = new CopyOnWriteArrayList<>();
//		}
//
//		public void bindFuture(@NotNull Future<?> future) {
//			Objects.requireNonNull(future, "future is null");
//
//			if (this.future != null)
//				throw new IllegalStateException("Current status is bound to future");
//
//			this.future = future;
//		}
//
//		public void setStageIndex(int index) {
//			Objects.checkIndex(index, this.stages.size());
//			this.stageIndex = index;
//		}
//
//		public int getStageIndex() {
//			return this.stageIndex;
//		}
//
//		public void nextStage() {
//			this.setStageIndex(this.stageIndex + 1);
//		}
//
//		@NotNull
//		@Unmodifiable
//		public List<Listener> getListeners() {
//			return this.listeners
//				.stream()
//				.map(IdentityRef::get)
//				.toList();
//		}
//
//		@NotNull
//		@UnmodifiableView
//		@Override
//		public List<Stage> getStages() {
//			return this.stages;
//		}
//
//		@NotNull
//		@Override
//		public Stage getCurrentStage() {
//			return this.stages.get(this.stageIndex);
//		}
//
//		@Override
//		public boolean isDone() {
//			return this.future.isDone();
//		}
//
//		@Override
//		public boolean isSuccess() {
//			return this.future.isDone() && FutureUtils.getException(this.future) == null;
//		}
//
//		@Nullable
//		@Override
//		public Throwable getException() {
//			return FutureUtils.getException(this.future);
//		}
//
//		@Override
//		public void await() {
//			try {
//				this.future.get();
//			} catch (InterruptedException e) {
//				Thread.currentThread().interrupt();
//			} catch (ExecutionException | CancellationException ignored) {
//			}
//		}
//
//		@Override
//		public void await(long timeout, @NotNull TimeUnit unit) {
//			Objects.requireNonNull(unit, "unit is null");
//
//			try {
//				this.future.get(timeout, unit);
//			} catch (InterruptedException e) {
//				Thread.currentThread().interrupt();
//			} catch (ExecutionException | TimeoutException | CancellationException ignored) {
//			}
//		}
//
//		@Override
//		public void addListener(@NotNull Listener listener) {
//			Objects.requireNonNull(listener, "listener is null");
//			this.listeners.add(IdentityRef.of(listener));
//		}
//
//		@Override
//		public void removeListener(@NotNull Listener listener) {
//			Objects.requireNonNull(listener, "listener is null");
//			this.listeners.remove(IdentityRef.of(listener));
//		}
//	}
}
