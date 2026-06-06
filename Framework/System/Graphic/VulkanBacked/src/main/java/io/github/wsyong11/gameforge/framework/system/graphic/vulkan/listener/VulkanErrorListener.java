package io.github.wsyong11.gameforge.framework.system.graphic.vulkan.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;

public interface VulkanErrorListener extends IListener {
	void onError(int code, @NotNull String message);
}
