package io.github.wsyong11.gameforge.framework.spi.registry;

import io.github.wsyong11.gameforge.framework.spi.ExtensionType;
import io.github.wsyong11.gameforge.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public abstract class ForwardingExtensionRegistry extends Wrapper<ExtensionRegistry> implements ExtensionRegistry {
	public ForwardingExtensionRegistry() { /* no-op */ }

	public ForwardingExtensionRegistry(@Nullable ExtensionRegistry delegate) {
		super(delegate);
	}

	@Override
	public <T> void register(@NotNull ExtensionType<T> type, @NotNull T instance) {
		this.delegate().register(type, instance);
	}

	@Override
	public <T> void unregister(@NotNull ExtensionType<T> type, @NotNull T instance) {
		this.delegate().unregister(type, instance);
	}

	@Override
	public <T> void unregister(@NotNull T instance) {
		this.delegate().unregister(instance);
	}

	@Override
	public <T> boolean has(@NotNull T extension) {
		return this.delegate().has(extension);
	}

	@Override
	public <T> boolean has(@NotNull ExtensionType<T> type) {
		return this.delegate().has(type);
	}

	@Override
	public <T> void setPriority(@NotNull ExtensionType<T> type, @NotNull T instance, int priority) {
		this.delegate().setPriority(type, instance, priority);
	}

	@Override
	public <T> int getPriority(@NotNull ExtensionType<T> type, @NotNull T instance) {
		return this.delegate().getPriority(type, instance);
	}

	@NotNull
	@Unmodifiable
	@Override
	public <T> List<T> getExtensions(@NotNull ExtensionType<T> type) {
		return this.delegate().getExtensions(type);
	}

	@Nullable
	@Override
	public <T> T getExtension(@NotNull ExtensionType<T> type) {
		return this.delegate().getExtension(type);
	}

	@Override
	public void clear() {
		this.delegate().clear();
	}

	@Override
	public void clear(@NotNull ExtensionType<?> type) {
		this.delegate().clear(type);
	}
}
