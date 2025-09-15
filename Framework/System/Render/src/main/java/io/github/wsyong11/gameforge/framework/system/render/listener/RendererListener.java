package io.github.wsyong11.gameforge.framework.system.render.listener;

import io.github.wsyong11.gameforge.framework.listener.IListener;
import io.github.wsyong11.gameforge.framework.system.render.Renderer;
import org.jetbrains.annotations.NotNull;

public interface RendererListener extends IListener {
	void onRendererRegister(@NotNull Renderer renderer);

	void onRendererUnregister(@NotNull Renderer renderer);
}
