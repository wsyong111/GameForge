package io.github.wsyong11.gameforge.framework.system.window.impl.glfw.graphic;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.window.WindowGraphicContext;
import io.github.wsyong11.gameforge.framework.system.window.ex.GraphicContextNotFound;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class GraphicContextFactory {
	private static final Map<Identifier, Function<Long, GLFWGraphicContext>> FACTORY_MAP = Map.of(
		WindowGraphicContext.OPENGL, OpenGLGraphicContext::new
	);

	@NotNull
	public static GLFWGraphicContext get(@NotNull Identifier api, long window) throws GraphicContextNotFound {
		Objects.requireNonNull(api, "api is null");

		Function<Long, GLFWGraphicContext> factory = FACTORY_MAP.get(api);
		if (factory == null)
			throw new GraphicContextNotFound(api);
		return factory.apply(window);
	}
}
