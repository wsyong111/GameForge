package io.github.wsyong11.gameforge.framework.system.graphic.vulkan;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.listener.VulkanErrorListener;
import io.github.wsyong11.gameforge.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.semver4j.Semver;

import java.util.*;

import static io.github.wsyong11.gameforge.framework.system.graphic.vulkan.VulkanUtils.queryWrapped;
import static io.github.wsyong11.gameforge.framework.system.graphic.vulkan.VulkanUtils.semverToVkVersion;
import static io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.VulkanErrors.checkVkResult;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3;

public class Vulkan extends VulkanObject<VkInstance> {
	private static final io.github.wsyong11.gameforge.framework.system.log.Logger LOGGER = io.github.wsyong11.gameforge.framework.system.log.Log.getLogger();

	@NotNull
	public static Vulkan.Builder builder() {
		return new Builder();
	}

	private final Lazy<List<PhysicalDevice>> physicalDevices;

	private final ListenerList listenerList;

	protected Vulkan(@NotNull VkInstance instance, @Nullable VkAllocationCallbacks allocator) {
		super(
			instance,
			allocator,
			null
		);

		this.physicalDevices = Lazy.of(this::enumPhysicalDevices);

		this.listenerList = ListenerList.sync();
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

	public void registerErrorListener(@NotNull VulkanErrorListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(VulkanErrorListener.class, listener);
	}

	public void unregisterErrorListener(@NotNull VulkanErrorListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(VulkanErrorListener.class, listener);
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

		private final Set<String> enabledLayers = new LinkedHashSet<>();
		private final Set<String> enabledExtensions = new LinkedHashSet<>();

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
		public Builder layer(@NotNull String name) {
			Objects.requireNonNull(name, "name is null");
			this.enabledLayers.add(name);
			return this;
		}

		@NotNull
		public Builder layers(@NotNull Collection<String> names) {
			Objects.requireNonNull(names, "names is null");
			this.enabledLayers.addAll(names);
			return this;
		}

		@NotNull
		public Builder disableLayer(@NotNull String name) {
			Objects.requireNonNull(name, "name is null");
			this.enabledLayers.remove(name);
			return this;
		}

		@NotNull
		public Builder disableLayers(@NotNull Collection<String> names) {
			Objects.requireNonNull(names, "names is null");
			this.enabledLayers.removeAll(names);
			return this;
		}

		@NotNull
		public Builder extension(@NotNull String name) {
			Objects.requireNonNull(name, "name is null");
			this.enabledExtensions.add(name);
			return this;
		}

		@NotNull
		public Builder extensions(@NotNull Collection<String> names) {
			Objects.requireNonNull(names, "names is null");
			this.enabledExtensions.addAll(names);
			return this;
		}

		@NotNull
		public Builder disableExtension(@NotNull String name) {
			Objects.requireNonNull(name, "name is null");
			this.enabledExtensions.remove(name);
			return this;
		}

		@NotNull
		public Builder disableExtensions(@NotNull Collection<String> names) {
			Objects.requireNonNull(names, "names is null");
			this.enabledExtensions.removeAll(names);
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
				Set<String> layers = new LinkedHashSet<>(this.enabledLayers);
				if (this.validation)
					layers.add("VK_LAYER_KHRONOS_validation");

				Set<String> extensions = new LinkedHashSet<>(this.enabledExtensions);
				if (this.debugUtils)
					extensions.add("VK_EXT_debug_utils");

				VkApplicationInfo appInfo = VkApplicationInfo
					.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
					.pApplicationName(stack.UTF8Safe(this.applicationName))
					.applicationVersion(semverToVkVersion(this.applicationVersion))
					.pEngineName(stack.UTF8Safe(this.engineName))
					.engineVersion(semverToVkVersion(this.engineVersion))
					.apiVersion(this.apiVersion)
					.pNext(NULL);

				VkInstanceCreateInfo info = VkInstanceCreateInfo
					.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
					.pApplicationInfo(appInfo)
					.flags(this.flags)
					.pNext(NULL);

				if (this.debugUtils) {
					VkDebugUtilsMessengerCreateInfoEXT debugInfo = VkDebugUtilsMessengerCreateInfoEXT
						.calloc(stack)
						.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
						.messageSeverity(
							0
						)
						.messageType(0)
						.pfnUserCallback((messageSeverity, messageTypes, pCallbackData, pUserData) -> 0);
				}

				PointerBuffer pInstance = stack.mallocPointer(1);
				checkVkResult(vkCreateInstance(info, this.allocator, pInstance));

				VkInstance instance = new VkInstance(pInstance.get(0), info);
				return new Vulkan(instance, this.allocator);
			}
		}
	}
}
