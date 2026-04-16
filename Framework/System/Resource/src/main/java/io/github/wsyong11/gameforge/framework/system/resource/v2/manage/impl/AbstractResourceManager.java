package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.framework.spi.registry.ExtensionRegistry;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceConflictResolver;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.util.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractResourceManager implements ResourceManager {
	private final ResourcePackRegistry packRegistry;
	private final ExtensionRegistry extensions;

	private volatile boolean closed;

	public AbstractResourceManager() {
		this.packRegistry = new ResourcePackRegistry();

		this.extensions = ExtensionRegistry.createRestrict(Set.of(
			ResourceConflictResolver.TYPE,
			ResourceTransformer.TYPE
		));

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

		handler.throwException("An error occurred while closing the resource", IOException::new);
	}
}
