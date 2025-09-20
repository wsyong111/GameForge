package io.github.wsyong11.gameforge.framework.system.render.shader;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ShaderManager {
	@Nullable
	Shader getShader(@NotNull Identifier id);

	void reloadShader(@NotNull Shader shader);

	int getShaderCount();

	@UnsafeAPI
	long getNativeHandle(@NotNull Shader shader);
}
