package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.platform.Platform;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceConflictResolver;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader.DefaultResourceLoader;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader.ResourceLoader;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.util.concurrent.SimpleThreadFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultResourceManager extends AbstractResourceManager {
	private static final Logger LOGGER = Log.getLogger();

	private final ExecutorService packReloadPool;

	public DefaultResourceManager() {
		int maxReloadPoolSize = Math.max(2, Math.min(Platform.CPU_COUNT * 2, 8));

		ThreadPoolExecutor packReloadPool = new ThreadPoolExecutor(
			maxReloadPoolSize,
			maxReloadPoolSize,
			10,
			TimeUnit.SECONDS,
			new LinkedBlockingDeque<>(),
			SimpleThreadFactory
				.builder()
				.name("PackLoadThread")
				.build()
		);
		packReloadPool.allowCoreThreadTimeOut(true);
		this.packReloadPool = packReloadPool;
	}

	@NotNull
	@Override
	public ResourceFileSystem getFileSystem() {
		return null;
	}

	@Nullable
	@Override
	public Identifier toIdentifier(@NotNull ResourcePath path) {
		return null;
	}

	@NotNull
	@Override
	public ResourcePath toPath(@NotNull Identifier id) {
		return null;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@Override
	protected ResourceLoader createLoader(
		@NotNull List<ResourcePack> packs,
		@NotNull List<ResourceConflictResolver> conflictResolvers,
		@NotNull List<ResourceTransformer> transformers
	) {
		return new DefaultResourceLoader(packs, conflictResolvers, transformers, this.packReloadPool);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	@Override
	public Resource getResource(@NotNull Identifier location) {
		return null;
	}

	@Nullable
	@Unmodifiable
	@Override
	public List<Resource> getAllResources(@NotNull Identifier location) {
		return List.of();
	}

	@Nullable
	@Unmodifiable
	@Override
	public List<String> listResources(@NotNull Identifier location) {
		return List.of();
	}

	@Override
	public boolean hasResource(@NotNull Identifier location) {
		return false;
	}

	// -------------------------------------------------------------------------------------------------------------- //


	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			this.packReloadPool.shutdown();
			try {
				if (this.packReloadPool.awaitTermination(1, TimeUnit.SECONDS))
					this.packReloadPool.shutdownNow();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
