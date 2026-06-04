package io.github.wsyong11.gameforge.framework.system.graphic.vulkan;

import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.semver4j.Semver;

import java.util.List;
import java.util.Objects;

import static io.github.wsyong11.gameforge.framework.system.graphic.vulkan.VulkanUtils.queryWrapped;
import static io.github.wsyong11.gameforge.framework.system.graphic.vulkan.VulkanUtils.semverToVkVersion;
import static io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.VulkanErrors.checkVkResult;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3;

public class Vulkan extends VulkanObject<VkInstance> {
	@NotNull
	public static Vulkan.Builder builder() {
		return new Builder();
	}

	private final Lazy<List<PhysicalDevice>> physicalDevices;

	protected Vulkan(@NotNull VkInstance instance, @Nullable VkAllocationCallbacks allocator) {
		super(
			instance,
			allocator,
			null
		);

		this.physicalDevices = Lazy.of(this::enumPhysicalDevices);
	}

	@NotNull
	@Unmodifiable
	private List<PhysicalDevice> enumPhysicalDevices() {
		VkInstance instance = this.requireNative();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			return queryWrapped(
				stack,
				instance,
				VK10::vkEnumeratePhysicalDevices,
				handle -> new PhysicalDevice(new VkPhysicalDevice(handle, instance), this)
			);
		}
	}

	@NotNull
	public VKCapabilitiesInstance getCapabilities() {
		VkInstance instance = this.requireNative();
		return instance.getCapabilities();
	}

	@NotNull
	@Unmodifiable
	public List<PhysicalDevice> getPhysicalDevices() {
		return this.physicalDevices.get();
	}

	@Override
	protected void freeImpl(@NotNull VkInstance instance, @Nullable VkAllocationCallbacks allocator) {
		vkDestroyInstance(instance, allocator);
	}

	@Override
	protected long getHandleImpl(@NotNull VkInstance instance) {
		return instance.address();
	}

	public static class Builder {
		private String applicationName = "Application";
		private Semver applicationVersion = Semver.ZERO;

		private String engineName = null;
		private Semver engineVersion = Semver.ZERO;

		private int apiVersion = VK_API_VERSION_1_3;

		private boolean validation = false;
		private boolean debugUtils = false;

		private int flags = 0;

		private VkAllocationCallbacks allocator = null;

		@NotNull
		public Builder applicationName(@NotNull String name) {
			Objects.requireNonNull(name, "name is null");
			this.applicationName = name;
			return this;
		}

		@NotNull
		public Builder applicationVersion(@NotNull Semver version) {
			Objects.requireNonNull(version, "version is null");
			this.applicationVersion = version;
			return this;
		}

		@NotNull
		public Builder engineName(@Nullable String name) {
			this.engineName = name;
			return this;
		}

		@NotNull
		public Builder engineVersion(@Nullable Semver version) {
			this.engineVersion = version == null ? Semver.ZERO : version;
			return this;
		}

		@NotNull
		public Builder apiVersion(int version) {
			this.apiVersion = version;
			return this;
		}

		@NotNull
		public Builder validation(boolean enable) {
			this.validation = enable;
			return this;
		}

		@NotNull
		public Builder validation() {
			return this.validation(true);
		}

		@NotNull
		public Builder debugUtils(boolean enable) {
			this.debugUtils = enable;
			return this;
		}

		@NotNull
		public Builder debugUtils() {
			return this.debugUtils(true);
		}

		@NotNull
		public Builder flags(int flags) {
			this.flags = flags;
			return this;
		}

		@NotNull
		public Builder allocator(@Nullable VkAllocationCallbacks allocator) {
			this.allocator = allocator;
			return this;
		}

		@NotNull
		public Vulkan build() {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				VkInstanceCreateInfo info = VkInstanceCreateInfo
					.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
					.pApplicationInfo(VkApplicationInfo
						.calloc(stack)
						.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
						.pApplicationName(stack.UTF8Safe(this.applicationName))
						.applicationVersion(semverToVkVersion(this.applicationVersion))
						.pEngineName(stack.UTF8Safe(this.engineName))
						.engineVersion(semverToVkVersion(this.engineVersion))
						.apiVersion(this.apiVersion)
						.pNext(NULL))
					.flags(this.flags)
					.pNext(NULL);

				PointerBuffer pInstance = stack.mallocPointer(1);
				checkVkResult(vkCreateInstance(info, this.allocator, pInstance));

				VkInstance instance = new VkInstance(pInstance.get(0), info);
				return new Vulkan(instance, this.allocator);
			}
		}
	}
}
