package io.github.wsyong11.gameforge.game.common.core.service;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.game.common.service.IService;
import io.github.wsyong11.gameforge.game.common.service.ServiceRegistry;
import io.github.wsyong11.gameforge.util.concurrent.ThreadUtils;
import io.github.wsyong11.gameforge.util.exception.RuntimeInterruptedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class ServiceRegistryImpl implements ServiceRegistry {
	private static final Logger LOGGER = Log.getLogger();

	private final Map<Identifier, IService> idServiceMap;
	private final Map<Class< ?>, IService> typeServiceMap;
	private final Object registerSignal;

	public ServiceRegistryImpl() {
		this.idServiceMap = new ConcurrentHashMap<>();
		this.typeServiceMap = new ConcurrentHashMap<>();

		this.registerSignal = new Object();
	}

	@Override
	public <T extends IService> void register(@NotNull Class<? super T> type, @NotNull T instance) {
		Objects.requireNonNull(instance, "instance is null");

		if (this.typeServiceMap.putIfAbsent(type, instance) != null)
			throw new IllegalStateException("Service " + type.getName() + " is registered");

		LOGGER.debug("Registered service {}: {}", type.getName(), lazy(instance));

		synchronized (this.registerSignal) {
			this.registerSignal.notifyAll();
		}
	}

	@Override
	public <T extends IService> void register(@NotNull Identifier id, @NotNull T instance) {
		Objects.requireNonNull(id, "id is null");
		Objects.requireNonNull(instance, "instance is null");

		if (this.idServiceMap.putIfAbsent(id, instance) != null)
			throw new IllegalStateException("Service " + id + " is registered");

		LOGGER.debug("Registered service {}: {}", id, lazy(instance));

		synchronized (this.registerSignal) {
			this.registerSignal.notifyAll();
		}
	}

	@Override
	public boolean unregister(@NotNull Class<? super IService> type) {
		Objects.requireNonNull(type, "type is null");

		boolean removed = this.typeServiceMap.remove(type) != null;
		if (removed)
			LOGGER.debug("Unregistered service {}", type.getName());
		return removed;
	}

	@Override
	public boolean unregister(@NotNull Identifier id) {
		Objects.requireNonNull(id, "id is null");
		boolean removed = this.idServiceMap.remove(id) != null;
		if (removed)
			LOGGER.debug("Unregistered service {}", id);
		return removed;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T extends IService> T getServiceUnsafe(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		return (T) this.typeServiceMap.get(type);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T extends IService> T getServiceUnsafe(@NotNull Identifier id) {
		Objects.requireNonNull(id, "id is null");
		return (T) this.idServiceMap.get(id);
	}

	@Nullable
	@Override
	public <T extends IService> T requireService(@NotNull Class<T> type, long timeoutMs) throws RuntimeInterruptedException {
		Objects.requireNonNull(type, "type is null");

		synchronized (this.registerSignal) {
			try {
				return ThreadUtils.waitValue(this.registerSignal, timeoutMs, () -> this.getServiceUnsafe(type));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeInterruptedException(e);
			} catch (TimeoutException e) {
				return null;
			}
		}
	}

	@Nullable
	@Override
	public <T extends IService> T requireService(@NotNull Identifier id, long timeoutMs) throws RuntimeInterruptedException {
		Objects.requireNonNull(id, "id is null");

		synchronized (this.registerSignal) {
			try {
				return ThreadUtils.waitValue(this.registerSignal, timeoutMs, () -> this.getServiceUnsafe(id));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeInterruptedException(e);
			} catch (TimeoutException e) {
				return null;
			}
		}
	}
}
