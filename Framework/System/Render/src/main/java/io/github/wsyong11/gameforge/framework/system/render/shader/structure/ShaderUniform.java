package io.github.wsyong11.gameforge.framework.system.render.shader.structure;

import io.github.wsyong11.gameforge.framework.system.render.shader.ShaderDataType;
import org.jetbrains.annotations.NotNull;

public interface ShaderUniform {
	@NotNull
	String getName();

	@NotNull
	ShaderDataType getType();

	int getLocation();
}
