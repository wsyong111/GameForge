package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.simple;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.TimeIt;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ReloadStatus;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceConflictResolver;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class DefaultResourceManager implements ResourceManager {
	private static final Logger LOGGER = Log.getLogger();

	private final ResourcePackRegistry packRegistry;
	private volatile boolean closed;

	public DefaultResourceManager() {
		this.packRegistry = new ResourcePackRegistry();

		this.closed = false;
	}

	private void ensureOpen() {
		if (this.closed)
			throw new IllegalStateException("Resource manager closed");
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

	@Override
	public void registerConflictResolver(@NotNull ResourceConflictResolver resolver) {

	}

	@Override
	public void unregisterConflictResolver(@NotNull ResourceConflictResolver resolver) {

	}

	@Override
	public void registerResourceTransformer(@NotNull ResourceTransformer transformer) {

	}

	@Override
	public void unregisterResourceTransformer(@NotNull ResourceTransformer transformer) {

	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@Override
	public ReloadStatus reload() {
		LOGGER.info("Start reload resource");
		try (TimeIt ignored = TimeIt.begin(LOGGER, LogLevel.INFO, "Reload complete")) {
			this.packRegistry.update();

			List<ResourcePack> packs = this.packRegistry.getCurrentPacks();

			LOGGER.debug("Pack info list:{}", lazy(() -> {
				StringBuilder sb = new StringBuilder();
				for (ResourcePack pack : packs) {
					sb.append('\n');
					sb.append("| ");
					sb.append(this.packRegistry.getPriority(pack));
					sb.append(' ');
					sb.append(pack.getClass().getName());
					sb.append(": \"");
					sb.append(pack.getSource());
					sb.append('"');
				}
				return sb.toString();
			}));

			SimpleResourceGraph graph = new SimpleResourceGraph();

			return null;
		}
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

	@Override
	public void close() throws IOException {
		this.closed = true;

		this.packRegistry.clearAll();
	}
}
