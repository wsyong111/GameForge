package io.github.wsyong11.gameforge.framework.system.render;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.render.ex.RenderSystemInitiationException;
import io.github.wsyong11.gameforge.framework.system.render.impl.opengl.OpenGL330RenderEngine;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.pack.AssetsResourcePack;
import io.github.wsyong11.gameforge.framework.system.window.Window;
import io.github.wsyong11.gameforge.framework.system.window.impl.glfw.GLFWWindowManager;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowInputListener;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowListener;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class Main {
	public static void main(String[] args) throws Throwable {
		ClassLoader classLoader = Main.class.getClassLoader();

		try {
			LogManager.setAdapter("log4j2");
			LogManager.bind(classLoader).getRootLoggerConfig().setLevel(LogLevel.TRACE);
			main();
		} finally {
			LogManager.unbind(classLoader);
		}
	}

	private static void main() throws RenderSystemInitiationException {
		try (ResourceManager resourceManager = new DefaultResourceManager(ResourcePath.of("assets"))) {
			resourceManager.addPack(new AssetsResourcePack("game"));
			resourceManager.reload();

			System.out.println("=".repeat(32));

			Vector2i logicSize = new Vector2i(800, 600);
			RenderSystem renderSystem = RenderSystem.init(
				resourceManager,
				logicSize,
				true,
				() -> OpenGL330RenderEngine::new,
				() -> GLFWWindowManager::new
			);

			Window window = renderSystem.getWindow();
			window.setTitle("Testing");
			window.addInputListener(new WindowInputListener() {
				@Override
				public void onKeyInput(@NotNull KeyCode code, int mods, @NotNull KeyAction action) {
					if (code == KeyCode.ESCAPE)
						renderSystem.runOnUIThread(RenderSystem::shutdown);
				}
			});
			window.addWindowListener(new WindowListener() {
				@Override
				public void onClose() {
					renderSystem.runOnUIThread(RenderSystem::shutdown);
				}
			});
			window.setWindowSize(logicSize);

			RenderSystem.loop();
		} finally {
			RenderSystem.shutdown();
		}
	}
}
