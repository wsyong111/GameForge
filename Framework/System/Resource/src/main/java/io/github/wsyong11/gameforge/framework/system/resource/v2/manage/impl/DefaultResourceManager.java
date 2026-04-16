package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.platform.Platform;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.TimeIt;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ReloadStatus;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceConflictResolver;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class DefaultResourceManager extends AbstractResourceManager {
	private static final Logger LOGGER = Log.getLogger();

	private final ExecutorService packReloadPool;

	public DefaultResourceManager() {
		this.packReloadPool = new ThreadPoolExecutor(
			1,
			Math.max(2, Math.min(Platform.CPU_COUNT / 2, 4)),
			10,
			TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(32)
		);
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
	private List<ResourcePath> reloadResourcePack(@NotNull ResourcePack pack) {

	}

	@NotNull
	private Map<ResourcePath, List<Resource>> reloadAndListResources(@NotNull List<ResourcePack> packs) {
		Objects.requireNonNull(packs, "packs is null");

		List<Future<List<ResourcePath>>> reloadFutures = packs
			.stream()
			.map(pack -> this.packReloadPool.submit(() -> {
				pack.load();
			}))
		this.packReloadPool.submit()
	}

	@NotNull
	private List<Resource> processConflict(@NotNull Map<ResourcePath, List<Resource>> resources) {
		return List.of();
	}

	@NotNull
	@Override
	protected ReloadStatus doReload(
		@NotNull List<ResourcePack> packs,
		@NotNull List<ResourceConflictResolver> conflictResolvers,
		@NotNull List<ResourceTransformer> transformers
	) {
		LOGGER.info("Start reload resource");
		try (TimeIt ignored = TimeIt.begin(LOGGER, LogLevel.INFO, "Reload complete")) {

			LOGGER.debug("Pack info list:{}", lazy(() -> {
				StringBuilder sb = new StringBuilder();
				for (ResourcePack pack : packs) {
					sb.append('\n');
					sb.append("| ");
					sb.append(this.getPackPriority(pack));
					sb.append(' ');
					sb.append(pack.getClass().getName());
					sb.append(": \"");
					sb.append(pack.getSource());
					sb.append('"');
				}
				return sb.toString();
			}));

			LOGGER.debug("Listing resources...");

			int totalResourceCount = 0;
			Map<ResourcePath, List<Resource>> allResources = new LinkedHashMap<>();

			for (ResourcePack pack : packs) {
				for (ResourcePath path : pack.list()) {

				}
				totalResourceCount++;
			}

			LOGGER.debug("Total found {} resources", );

			this.processConflict()

		}
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
