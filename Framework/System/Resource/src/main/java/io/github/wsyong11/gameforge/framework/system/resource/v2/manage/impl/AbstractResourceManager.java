package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import com.google.common.util.concurrent.ListenableFuture;
import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.framework.spi.registry.ExtensionRegistry;
import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ReloadStatus;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceConflictResolver;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.fs.ResourceGraphFileSystem;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader.ResourceLoader;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.util.concurrent.FutureUtils;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractResourceManager implements ResourceManager {
	private final ResourcePackRegistry packRegistry;
	private final ExtensionRegistry extensions;

	@Nullable
	private volatile ResourceLoader loader;

	private final AtomicReference<ResourceContext> context;

	private final AtomicBoolean closed;

	public AbstractResourceManager() {
		this.packRegistry = new ResourcePackRegistry();

		this.extensions = ExtensionRegistry.createRestrict(Set.of(
			ResourceConflictResolver.TYPE,
			ResourceTransformer.TYPE
		));

		this.loader = null;

		this.context = new AtomicReference<>(null);

		this.closed = new AtomicBoolean(false);
	}

	@NotNull
	protected ResourcePackRegistry getPackRegistry() {
		return this.packRegistry;
	}

	@NotNull
	protected ExtensionRegistry getExtensions() {
		return this.extensions;
	}

	@Nullable
	protected ResourceContext getContext() {
		return this.context.get();
	}

	@NotNull
	protected ResourceContext requireContext() {
		ResourceContext context = this.getContext();
		if (context == null)
			throw new IllegalStateException("Resource not load");

		return context;
	}

	protected void ensureOpen() {
		if (this.closed.get())
			throw new IllegalStateException("Resource manager closed");
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@Override
	public ResourceFileSystem getFileSystem() {
		return this.requireContext().getFileSystem();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	protected abstract ResourceLoader createLoader(
		@NotNull List<ResourcePack> packs,
		@NotNull List<ResourceConflictResolver> conflictResolvers,
		@NotNull List<ResourceTransformer> transformers
	);

	protected void onLoadComplete(@NotNull ResourceGraph graph) {
		Objects.requireNonNull(graph, "graph is null");
		this.context.set(new ResourceContext(graph));  // TODO: 2026/4/18 Frozen graph
	}

	protected void onLoadFailed(@NotNull Throwable exception) {
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void processLoadComplete(@NotNull Future<ResourceGraph> future) {
		Objects.requireNonNull(future, "future is null");

		ResourceGraph graph;
		try {
			graph = future.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		} catch (ExecutionException e) {
			this.onLoadFailed(e.getCause());
			return;
		} catch (CancellationException ignored) {
			return;
		} finally {
			synchronized (this) {
				this.loader = null;
			}
		}

		this.onLoadComplete(graph);
	}


	@NotNull
	@Override
	public synchronized ReloadStatus reload() {
		this.ensureOpen();

		ResourceLoader loader = this.loader;
		if (loader != null)
			throw new NotImplementedException(); // TODO: 2026/4/18 Cancel and restart reload

		this.packRegistry.update();

		List<ResourceConflictResolver> conflictResolvers = this.extensions.getExtensions(ResourceConflictResolver.TYPE);
		List<ResourceTransformer> transformers = this.extensions.getExtensions(ResourceTransformer.TYPE);

		List<ResourcePack> packs = this.packRegistry.getCurrentPacks();

		ResourceLoader newLoader = this.createLoader(packs, conflictResolvers, transformers);
		ListenableFuture<ResourceGraph> future = newLoader.getFuture();
		future.addListener(() -> this.processLoadComplete(future), Runnable::run);

		this.loader = newLoader;
		newLoader.load();

		return newLoader.getStatus();
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
		if (!this.closed.compareAndSet(false, true))
			return;

		ResourceLoader loader = this.loader;
		if (loader != null)
			FutureUtils.cancelAwait(loader.getFuture(), 10, TimeUnit.SECONDS);
		this.context.set(null);

		ExceptionHandler handler = new ExceptionHandler();
		for (ResourcePack pack : this.packRegistry.getPacks())
			handler.close(pack);

		this.packRegistry.clearAll();
		this.extensions.clear();

		handler.throwException("An error occurred while closing", IOException::new);
	}

	protected static final class ResourceContext {
		private final ResourceGraph graph;
		private final ResourceFileSystem fileSystem;

		public ResourceContext(@NotNull ResourceGraph graph) {
			Objects.requireNonNull(graph, "graph is null");
			this.graph = graph;
			this.fileSystem = new ResourceGraphFileSystem(graph);
		}

		@NotNull
		public ResourceGraph getGraph() {
			return this.graph;
		}

		@NotNull
		public ResourceFileSystem getFileSystem() {
			return this.fileSystem;
		}
	}
}
