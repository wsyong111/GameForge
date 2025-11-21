package io.github.wsyong11.gameforge.framework.system.render.shader;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.shader.structure.ShaderStructure;
import org.jetbrains.annotations.NotNull;

public interface Shader {
	@NotNull
	Identifier getId();

	void reload();

	@NotNull
	ShaderStructure getStructure();
}
