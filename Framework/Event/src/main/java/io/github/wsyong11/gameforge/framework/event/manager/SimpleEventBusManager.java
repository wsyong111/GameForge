package io.github.wsyong11.gameforge.framework.event.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.event.EventBus;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class SimpleEventBusManager implements EventBusManager {
	private static final Logger LOGGER = Log.getLogger();

	private final Map<Identifier, EventBus> eventBusMap;

	public SimpleEventBusManager() {
		this.eventBusMap = new ConcurrentHashMap<>();
	}

	@NotNull
	protected ExecutorService createDefaultExecutor(@NotNull Identifier id, @NotNull EventBus eventBus) {
		LOGGER.debug("Create default executor from event bus {}", id);
		return new ThreadPoolExecutor(
			1,
			32,
			30L,
			TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(256),
			new ThreadFactoryBuilder()
				.setDaemon(true)
				.setNameFormat("EventBus-" + id + "-%d")
				.build(),
			new ThreadPoolExecutor.CallerRunsPolicy()
		);
	}

	@Override
	public void registerEventBus(@NotNull Identifier id, @NotNull EventBus eventBus) {
		Objects.requireNonNull(id, "id is null");
		Objects.requireNonNull(eventBus, "eventBus is null");

		if (this.eventBusMap.putIfAbsent(id, eventBus) != null)
			throw new IllegalArgumentException("Event bus id " + id + " is registered");

		if (eventBus.getDefaultExecutor() == null)
			eventBus.setDefaultExecutor(this.createDefaultExecutor(id, eventBus));

		LOGGER.debug("Registered event bus {}: {}", id, lazy(eventBus));
	}

	@Override
	public boolean unregister(@NotNull Identifier id) {
		Objects.requireNonNull(id, "id is null");
		boolean removed = this.eventBusMap.remove(id) != null;
		if (removed)
			LOGGER.debug("Unregistered event bus {}", id);
		return removed;
	}

	@NotNull
	@Unmodifiable
	@Override
	public Set<Identifier> getRegistered() {
		return Set.copyOf(this.eventBusMap.keySet());
	}

	@Nullable
	@Override
	public EventBus getEventBus(@NotNull Identifier id) {
		Objects.requireNonNull(id, "id is null");
		return this.eventBusMap.get(id);
	}

	@Override
	public void close() {
		for (Map.Entry<Identifier, EventBus> entry : this.eventBusMap.entrySet()) {
			Identifier id = entry.getKey();
			EventBus eventBus = entry.getValue();

			LOGGER.debug("Cleaning event bus {}", id);

			eventBus.unregisterAll();

			ExecutorService defaultExecutor = eventBus.getDefaultExecutor();
			if (defaultExecutor != null && !defaultExecutor.isShutdown()) {
				defaultExecutor.shutdownNow();

				try {
					if (!defaultExecutor.awaitTermination(5, TimeUnit.SECONDS))
						LOGGER.warn("Executor for {} did not terminate in time", id);

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					LOGGER.warn("Interrupted while shutting down executor for {}", id);
				}
			}
		}
	}
}
