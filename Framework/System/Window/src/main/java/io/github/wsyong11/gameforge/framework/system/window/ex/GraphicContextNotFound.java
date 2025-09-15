package io.github.wsyong11.gameforge.framework.system.window.ex;

import io.github.wsyong11.gameforge.framework.Identifier;
import org.jetbrains.annotations.NotNull;

public class GraphicContextNotFound extends WindowCreatingException {
	public GraphicContextNotFound(@NotNull Identifier id) {
		super("Available Graphic Context wasn't found: " + id);
	}
}
