package io.github.wsyong11.gameforge.framework.system.graphic.vulkan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.vulkan.VkAllocationCallbacks;

public abstract class VulkanObject<T> extends VulkanHandle<T> implements AutoCloseable {
	public VulkanObject(@NotNull T instance, @NotNull VulkanHandle<?> parent) {
		super(instance, parent);
	}

	protected VulkanObject(@NotNull T instance, @Nullable VkAllocationCallbacks allocator, @Nullable VulkanObject<?> parent) {
		super(instance, allocator, parent);
	}

	protected abstract void freeImpl(@NotNull T instance, @Nullable VkAllocationCallbacks allocator);

	@Override
	public void close() {
		T instance = this.setNative(null);
		if (instance != null)
			this.freeImpl(instance, this.getAllocator());
	}
}
