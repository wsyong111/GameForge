package io.github.wsyong11.gameforge.framework.system.render.impl.opengl.shader;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.shader.Shader;
import io.github.wsyong11.gameforge.framework.system.render.shader.structure.ShaderStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GLShader implements Shader {
	private final Identifier id;

	public GLShader(@NotNull Identifier id) {
		Objects.requireNonNull(id, "id is null");

		this.id = id;
	}

	@NotNull
	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public void reload() {

	}

	@NotNull
	@Override
	public ShaderStructure getStructure() {
		return null;
	}
}
