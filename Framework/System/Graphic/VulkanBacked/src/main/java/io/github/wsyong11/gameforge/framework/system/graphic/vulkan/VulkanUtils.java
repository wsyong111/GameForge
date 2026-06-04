package io.github.wsyong11.gameforge.framework.system.graphic.vulkan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VKCapabilitiesInstance;
import org.lwjgl.vulkan.VkDebugUtilsObjectNameInfoEXT;
import org.lwjgl.vulkan.VkDevice;
import org.semver4j.Semver;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static io.github.wsyong11.gameforge.framework.system.graphic.vulkan.ex.VulkanErrors.checkVkResult;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_STRUCTURE_TYPE_DEBUG_UTILS_OBJECT_NAME_INFO_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.vkSetDebugUtilsObjectNameEXT;
import static org.lwjgl.vulkan.VK10.*;

class VulkanUtils {
	public static void setDebugName(@NotNull VkDevice device, long instance, int type, @Nullable String name) {
		VKCapabilitiesInstance capabilitiesInstance = device.getCapabilitiesInstance();
		if (capabilitiesInstance.vkSetDebugUtilsObjectNameEXT == NULL)
			return;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkDebugUtilsObjectNameInfoEXT info = VkDebugUtilsObjectNameInfoEXT
				.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_OBJECT_NAME_INFO_EXT)
				.objectType(type)
				.objectHandle(instance)
				.pObjectName(stack.UTF8Safe(name));

			vkSetDebugUtilsObjectNameEXT(device, info);
		}
	}

	@NotNull
	public static <T> PointerBuffer query(@NotNull MemoryStack stack, @NotNull T object, @NotNull QueryCallback<T> callback) {
		Objects.requireNonNull(stack, "stack is null");
		Objects.requireNonNull(object, "object is null");
		Objects.requireNonNull(callback, "callback is null");

		IntBuffer pCount = stack.mallocInt(1);
		checkVkResult(callback.query(object, pCount, null));

		PointerBuffer pResult = stack.mallocPointer(pCount.get(0));
		checkVkResult(callback.query(object, pCount, pResult));

		return pResult;
	}

	@NotNull
	@Unmodifiable
	public static <T, R> List<R> queryWrapped(
		@NotNull MemoryStack stack,
		@NotNull T object,
		@NotNull QueryCallback<T> callback,
		@NotNull Function<Long, R> mapper
	) {
		Objects.requireNonNull(stack, "stack is null");
		Objects.requireNonNull(object, "object is null");
		Objects.requireNonNull(callback, "callback is null");
		Objects.requireNonNull(mapper, "mapper is null");

		PointerBuffer pResult = query(stack, object, callback);

		List<R> results = new ArrayList<>();
		for (int i = 0; i < pResult.capacity(); i++) {
			long handle = pResult.get(i);
			results.add(mapper.apply(handle));
		}

		return Collections.unmodifiableList(results);
	}

	public interface QueryCallback<T> {
		int query(@NotNull T object, @NotNull IntBuffer pCount, @Nullable PointerBuffer pResult);
	}

	public static int semverToVkVersion(@NotNull Semver ver) {
		return VK_MAKE_API_VERSION(0, ver.getMajor(), ver.getMinor(), ver.getPatch());
	}

	@NotNull
	public static Semver vkVersionToSemver(int ver) {
		return Semver.create(
			VK_API_VERSION_MAJOR(ver),
			VK_API_VERSION_MINOR(ver),
			VK_API_VERSION_PATCH(ver)
		);
	}
}
