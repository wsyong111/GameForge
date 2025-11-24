package io.github.wsyong11.gameforge.framework.system.render.shader;

import io.github.wsyong11.gameforge.framework.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ShaderManager {
	void preload(@NotNull Identifier id);

	@Nullable
	Shader getShader(@NotNull Identifier id);

	void reloadShaders();

	int getShaderCount();
}
