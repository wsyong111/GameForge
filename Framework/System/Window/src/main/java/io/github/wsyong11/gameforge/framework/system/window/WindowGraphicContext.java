package io.github.wsyong11.gameforge.framework.system.window;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.annotation.ThreadSensitive;
import org.jetbrains.annotations.NotNull;

public interface WindowGraphicContext {
	Identifier OPENGL = Identifier.withDefaultNamespace("opengl");
	Identifier VULKAN = Identifier.withDefaultNamespace("vulkan");

	boolean isAvailable();

	@NotNull
	Identifier getType();

	void setVSyncType(@NotNull VSyncType type);

	@NotNull
	VSyncType getVSyncType();

	@ThreadSensitive
	void bind();

	@ThreadSensitive
	void unbind();

	void swap();
}
