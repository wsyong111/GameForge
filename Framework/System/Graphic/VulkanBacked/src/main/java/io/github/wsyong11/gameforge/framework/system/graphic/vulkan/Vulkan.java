package io.github.wsyong11.gameforge.framework.system.graphic.vulkan;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug.DebugCallbackInfo;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug.DebugMessageSeverity;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug.DebugMessageType;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.listener.VulkanErrorListener;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.Lazy;
import io.github.wsyong11.gameforge.util.enumerate.BitEnums;
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
	private static final Logger LOGGER = Log.getLogger();

	@NotNull
	public static Vulkan.Builder builder() {
		return new Builder();
	}

	@Nullable
	private final DebugUtilsCallback debugUtilsCallback;

	private final Lazy<List<PhysicalDevice>> physicalDevices;

	private final ListenerList listenerList;

	protected Vulkan(@NotNull VkInstance instance, @Nullable VkAllocationCallbacks allocator, @Nullable DebugUtilsCallback debugUtilsCallback) {
		super(
			instance,
			allocator,
			null
		);

		this.debugUtilsCallback = debugUtilsCallback;

		this.physicalDevices = Lazy.of(this::enumPhysicalDevices);

		this.listenerList = ListenerList.sync();

		if (debugUtilsCallback != null)
			debugUtilsCallback.setCallback(this::onDebugUtilsCallbackInvoke);
	}

	private boolean onDebugUtilsCallbackInvoke(
		@NotNull DebugMessageSeverity severity,
		@NotNull Set<DebugMessageType> types,
		@NotNull VkDebugUtilsMessengerCallbackDataEXT data
	) {
		Objects.requireNonNull(severity, "severity is null");
		Objects.requireNonNull(types, "types is null");
		Objects.requireNonNull(data, "data is null");



		DebugCallbackInfo info = new DebugCallbackInfo(
			severity,
			Collections.unmodifiableSet(types),
			data.pMessageString(),
			data.pMessageIdNameString(),
			data.messageIdNumber(),
			Collections.unmodifiableList(queueLabels),
			commandBufferLabels,
			objects
		);

		return false;
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
		if (this.debugUtilsCallback != null)
			this.debugUtilsCallback.free();
	}

	@Override
	protected long getHandleImpl(@NotNull VkInstance instance) {
		return instance.address();
	}

	protected static class DebugUtilsCallback extends VkDebugUtilsMessengerCallbackEXT {
		private Callback callback;

		public DebugUtilsCallback() {
			this.callback = null;
		}

		@Nullable
		public Callback getCallback() {
			return this.callback;
		}

		public void setCallback(Callback callback) {
			this.callback = callback;
		}

		@Override
		public int invoke(int messageSeverity, int messageTypes, long pCallbackData, long pUserData) {
			if (this.callback == null)
				return VK_FALSE;

			VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
			DebugMessageSeverity severity = BitEnums.fromBitFirst(DebugMessageSeverity.class, messageSeverity);
			Set<DebugMessageType> types = BitEnums.fromBit(DebugMessageType.class, messageTypes);

			assert severity != null : "severity is null";

			boolean result = this.callback.onInvoke(
				severity,
				types,
				callbackData
			);

			return result ? VK_TRUE : VK_FALSE;
		}

		@FunctionalInterface
		public interface Callback {
			boolean onInvoke(
				@NotNull DebugMessageSeverity severity,
				@NotNull Set<DebugMessageType> types,
				@NotNull VkDebugUtilsMessengerCallbackDataEXT data
			);
		}
	}


	public static class Builder {
		private String applicationName = "Application";
		private Semver applicationVersion = Semver.ZERO;

		private String engineName = null;
		private Semver engineVersion = Semver.ZERO;

		private int apiVersion = VK_API_VERSION_1_3;

		private boolean validation = false;
		private boolean debugUtils = false;
		private final Set<DebugMessageSeverity> debugMessageSeverities = EnumSet.of(
			DebugMessageSeverity.WARNING,
			DebugMessageSeverity.ERROR
		);
		private final Set<DebugMessageType> debugMessageTypes = EnumSet.of(
			DebugMessageType.GENERAL,
			DebugMessageType.VALIDATION
		);

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
		public Builder debugMessageSeverities(@NotNull Collection<DebugMessageSeverity> severities) {
			Objects.requireNonNull(severities, "severities is null");
			this.debugMessageSeverities.clear();
			this.debugMessageSeverities.addAll(severities);
			return this;
		}

		@NotNull
		public Builder debugMessageTypes(@NotNull Collection<DebugMessageType> types) {
			Objects.requireNonNull(types, "types is null");
			this.debugMessageTypes.clear();
			this.debugMessageTypes.addAll(types);
			return this;
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
			Set<String> layers = new LinkedHashSet<>(this.enabledLayers);
			if (this.validation)
				layers.add("VK_LAYER_KHRONOS_validation");

			Set<String> extensions = new LinkedHashSet<>(this.enabledExtensions);
			if (this.debugUtils)
				extensions.add("VK_EXT_debug_utils");

			try (MemoryStack stack = MemoryStack.stackPush()) {
				PointerBuffer ppLayers = stack.mallocPointer(layers.size());
				for (String layer : layers)
					ppLayers.put(stack.UTF8(layer));
				ppLayers.flip();

				PointerBuffer ppExtensions = stack.mallocPointer(extensions.size());
				for (String extension : extensions)
					ppExtensions.put(stack.UTF8(extension));
				ppExtensions.flip();

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
					.flags(this.flags)
					.ppEnabledExtensionNames(ppExtensions)
					.ppEnabledLayerNames(ppLayers)
					.pApplicationInfo(appInfo)
					.pNext(NULL);

				DebugUtilsCallback debugUtilsCallback;
				if (this.debugUtils) {
					debugUtilsCallback = new DebugUtilsCallback();

					VkDebugUtilsMessengerCreateInfoEXT debugInfo = VkDebugUtilsMessengerCreateInfoEXT
						.calloc(stack)
						.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
						.messageSeverity(BitEnums.toBit(this.debugMessageSeverities))
						.messageType(BitEnums.toBit(this.debugMessageTypes))
						.pfnUserCallback(debugUtilsCallback);

					info.pNext(debugInfo);
				} else {
					debugUtilsCallback = null;
				}

				PointerBuffer pInstance = stack.mallocPointer(1);
				checkVkResult(vkCreateInstance(info, this.allocator, pInstance));

				VkInstance instance = new VkInstance(pInstance.get(0), info);
				return new Vulkan(instance, this.allocator, debugUtilsCallback);
			}
		}
	}
}
