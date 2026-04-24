package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.platform.Platform;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceConflictResolver;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader.DefaultResourceLoader;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.loader.ResourceLoader;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.util.concurrent.ExecutorServiceUtils;
import io.github.wsyong11.gameforge.util.concurrent.SimpleThreadFactory;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultResourceManager extends AbstractResourceManager {
	private static final Logger LOGGER = Log.getLogger();

	private final ThreadPoolExecutor packLoadThreadPool;
	private final Scheduler packLoadScheduler;

	private final ThreadPoolExecutor transformThreadPool;
	private final Scheduler transformScheduler;

	public DefaultResourceManager() {
		int packLoadThreadCount = Math.max(2, Math.min(Platform.CPU_COUNT * 2, 8));
		this.packLoadThreadPool = new ThreadPoolExecutor(
			packLoadThreadCount,
			packLoadThreadCount * 4,
			10,
			TimeUnit.SECONDS,
			new LinkedBlockingDeque<>(8),
			SimpleThreadFactory
				.builder()
				.name("PackLoaderThread")
				.build()
		);
		this.packLoadScheduler = Schedulers.from(this.packLoadThreadPool);

		int transformThreadCount = Math.max(1, Math.min(Platform.CPU_COUNT - 1, 8));
		this.transformThreadPool = new ThreadPoolExecutor(
			transformThreadCount,
			transformThreadCount * 2,
			10,
			TimeUnit.SECONDS,
			new LinkedBlockingDeque<>(32),
			SimpleThreadFactory
				.builder()
				.name("TransformThread")
				.build()
		);
		this.transformScheduler = Schedulers.from(this.transformThreadPool);
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
		Objects.requireNonNull(packs, "packs is null");
		Objects.requireNonNull(conflictResolvers, "conflictResolvers is null");
		Objects.requireNonNull(transformers, "transformers is null");

		return new DefaultResourceLoader(
			packs,
			conflictResolvers,
			transformers,
			this.packLoadScheduler,
			this.packLoadThreadPool.getCorePoolSize(),
			this.transformScheduler,
			this.transformThreadPool.getCorePoolSize()
		);
	}

	@Override
	protected void onLoadFailed(@NotNull Throwable exception) {
		super.onLoadFailed(exception);
		LOGGER.error("Failed to load resources", exception);
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
			ExecutorServiceUtils.shutdown(this.packLoadThreadPool, 1, TimeUnit.SECONDS);
			ExecutorServiceUtils.shutdown(this.transformThreadPool, 1, TimeUnit.SECONDS);
		}
	}
}
