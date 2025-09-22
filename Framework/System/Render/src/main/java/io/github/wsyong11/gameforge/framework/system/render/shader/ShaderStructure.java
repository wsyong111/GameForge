package io.github.wsyong11.gameforge.framework.system.render.shader;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ShaderStructure {
	@NotNull
	List<Uniform> getUniforms();
}
