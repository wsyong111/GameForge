package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.system.graphic.vulkan.debug.DebugCallbackInfo;
import org.jetbrains.annotations.NotNull;

public interface VulkanDebugListener extends IListener {
	void onError(@NotNull DebugCallbackInfo info);
}
