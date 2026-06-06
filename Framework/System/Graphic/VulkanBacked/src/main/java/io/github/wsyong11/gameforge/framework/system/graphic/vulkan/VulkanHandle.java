package io.github.wsyong11.gameforge.framework.system.graphic.vulkan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.vulkan.VkAllocationCallbacks;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class VulkanHandle<T> {
	private final AtomicReference<T> instance;
	@Nullable
	private final VkAllocationCallbacks allocator;
	@Nullable
	private final VulkanHandle<?> parent;

	@Nullable
	private volatile String debugName;

	protected VulkanHandle(@NotNull T instance, @NotNull VulkanHandle<?> parent) {
		this(
			instance,
			parent.getAllocator(),
			parent
		);
	}

	protected VulkanHandle(@NotNull T instance, @Nullable VkAllocationCallbacks allocator, @Nullable VulkanHandle<?> parent) {
		Objects.requireNonNull(instance, "instance is null");

		this.instance = new AtomicReference<>(instance);
		this.allocator = allocator;
		this.parent = parent;

		this.debugName = null;
	}

	protected abstract long getHandleImpl(@NotNull T instance);

	protected void setDebugNameImpl(@NotNull T instance, @Nullable String name) { /* no-op */ }

	@Nullable
	public VulkanHandle<?> getParent() {
		return this.parent;
	}

	public void setDebugName(@Nullable String name) {
		T instance = this.instance.get();
		if (instance == null)
			return;

		this.debugName = name;
		this.setDebugNameImpl(instance, name);
	}

	@Nullable
	public String getDebugName() {
		return this.debugName;
	}

	@Nullable
	public VkAllocationCallbacks getAllocator() {
		return this.allocator;
	}

	public boolean isClosed() {
		return this.instance.get() == null;
	}

	@NotNull
	protected T requireNative() {
		T value = this.instance.get();
		if (value == null)
			throw new IllegalStateException("Vulkan object already destroyed");
		return value;
	}

	@Nullable
	protected T setNative(@Nullable T instance) {
		return this.instance.getAndSet(instance);
	}

	@Nullable
	public T getNative() {
		return this.instance.get();
	}

	public long getHandle() {
		T instance = this.instance.get();
		return instance == null ? NULL : this.getHandleImpl(instance);
	}

	@Override
	public String toString() {
		long address = this.getHandle();
		String debugName = this.debugName;

		String type = this.getClass().getSimpleName();

		String namePart = (debugName != null)
			? "\"" + debugName + "\""
			: "<unnamed>";

		String handlePart = address == NULL
			? "destroyed"
			: Long.toHexString(address).toUpperCase(Locale.ROOT);

		return type + "(" + namePart + ", " + handlePart + ")";
	}
}
