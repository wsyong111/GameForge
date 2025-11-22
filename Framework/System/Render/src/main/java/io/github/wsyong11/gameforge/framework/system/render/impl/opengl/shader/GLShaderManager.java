package io.github.wsyong11.gameforge.framework.system.render.impl.opengl.shader;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.render.engine.RenderSystemContext;
import io.github.wsyong11.gameforge.framework.system.render.shader.Shader;
import io.github.wsyong11.gameforge.framework.system.render.shader.ShaderManager;
import io.github.wsyong11.gameforge.framework.system.resource.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GLShaderManager implements ShaderManager {
	private final RenderSystemContext renderSystemContext;
	private final ResourceProvider resourceProvider;

	public GLShaderManager(@NotNull RenderSystemContext renderSystemContext, @NotNull ResourceProvider resourceProvider) {
		Objects.requireNonNull(renderSystemContext, "renderSystemContext is null");
		Objects.requireNonNull(resourceProvider, "resourceProvider is null");

		this.renderSystemContext = renderSystemContext;
		this.resourceProvider = resourceProvider;
	}

	@Override
	public void preload(@NotNull Identifier id) {

	}

	@Nullable
	@Override
	public Shader getShader(@NotNull Identifier id) {
		return null;
	}

	@Override
	public void reloadShaders() {

	}

	@Override
	public int getShaderCount() {
		return 0;
	}
}
