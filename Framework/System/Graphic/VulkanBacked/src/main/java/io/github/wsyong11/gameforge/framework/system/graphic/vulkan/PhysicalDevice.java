package io.github.wsyong11.gameforge.framework.system.graphic.vulkan;

import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import java.util.Objects;

public class PhysicalDevice extends VulkanHandle<VkPhysicalDevice> {
	private final Lazy<PhysicalDeviceProperties> properties;

	public PhysicalDevice(@NotNull VkPhysicalDevice instance, @NotNull Vulkan parent) {
		super(
			Objects.requireNonNull(instance, "instance is null"),
			parent
		);

		this.properties = Lazy.of(this::queryProperties);
	}

	@NotNull
	private PhysicalDeviceProperties queryProperties() {
		VkPhysicalDevice instance = this.requireNative();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkPhysicalDeviceProperties deviceProperties = VkPhysicalDeviceProperties.malloc(stack);
			VK10.vkGetPhysicalDeviceProperties(instance, deviceProperties);

			return PhysicalDeviceProperties.of(deviceProperties);
		}
	}

	@NotNull
	public PhysicalDeviceProperties getProperties() {
		return this.properties.get();
	}

	@Nullable
	@Override
	public Vulkan getParent() {
		return (Vulkan) super.getParent();
	}

	@Override
	protected long getHandleImpl(@NotNull VkPhysicalDevice instance) {
		return instance.address();
	}

}
