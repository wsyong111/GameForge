package io.github.wsyong11.gameforge.game.common.core.service;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.listener.ReloadListener;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.pack.ResourcePack;
import io.github.wsyong11.gameforge.game.common.service.ResourceManagerService;
import io.github.wsyong11.gameforge.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("resource")
public class ResourceManagerServiceStub extends Wrapper<ResourceManager> implements ResourceManagerService {
	public ResourceManagerServiceStub(@NotNull ResourceManager delegate) {
		super(Objects.requireNonNull(delegate, "delegate is null"));
	}

	@Override
	public void registerReloadListener(@NotNull ReloadListener listener) {
		this.delegate().registerReloadListener(listener);
	}

	@Override
	public void unregisterReloadListener(@NotNull ReloadListener listener) {
		this.delegate().unregisterReloadListener(listener);
	}

	@Override
	public void registerReloadCallback(@NotNull Runnable callback) {
		this.delegate().registerReloadCallback(callback);
	}

	@Override
	public void unregisterReloadCallback(@NotNull Runnable callback) {
		this.delegate().unregisterReloadCallback(callback);
	}

	@NotNull
	@UnmodifiableView
	@Override
	public List<ResourcePack> getResourcePacks() {
		return this.delegate().getResourcePacks();
	}

	@Nullable
	@Override
	public Resource getResource(@NotNull Identifier name) {
		return this.delegate().getResource(name);
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<Resource> getResources(@NotNull Identifier name) {
		return this.delegate().getResources(name);
	}

	@NotNull
	@Override
	public Optional<Resource> getResourceOptional(@NotNull Identifier name) {
		return this.delegate().getResourceOptional(name);
	}

	@Override
	public boolean hasResource(@NotNull Identifier name) {
		return this.delegate().hasResource(name);
	}

	@UnsafeAPI
	@Nullable
	@Override
	public ResourceManager getDelegate() {
		return super.getDelegate();
	}
}
