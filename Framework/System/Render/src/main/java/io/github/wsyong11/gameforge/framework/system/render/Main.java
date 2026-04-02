package io.github.wsyong11.gameforge.framework.system.render;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.render.context.RenderContext;
import io.github.wsyong11.gameforge.framework.system.render.mesh.Mesh;
import io.github.wsyong11.gameforge.framework.system.render.mesh.ex.MeshLoadException;
import io.github.wsyong11.gameforge.framework.system.render.mesh.loader.ObjMeshLoader;
import io.github.wsyong11.gameforge.framework.system.render.renderer.Renderer;
import io.github.wsyong11.gameforge.framework.system.render.renderer.RendererContext;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.pack.AssetsResourcePack;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Objects;

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

	private static void main() throws Throwable {
		try (ResourceManager resourceManager = new DefaultResourceManager(ResourcePath.of("assets"))) {
			resourceManager.addPack(new AssetsResourcePack("game"));
			resourceManager.reload();

			try (InputStream stream = resourceManager.getResource(Identifier.withDefaultNamespace("model/teapot.obj")).openStream()) {
				new ObjMeshLoader().load(stream);
			} catch (MeshLoadException e) {
				Throwable cause = e.getCause();
				if (cause instanceof SyntaxException ex)
					System.err.println(ex.getFormattedMessage());
			}

//			System.out.println("=".repeat(32));
//
//			Vector2i logicSize = new Vector2i(800, 600);
//			RenderSystem renderSystem = RenderSystem.init(
//				resourceManager,
//				logicSize,
//				true,
//				() -> OpenGL330RenderEngine::new,
//				() -> GLFWWindowManager::new
//			);
//
//			Window window = renderSystem.getWindow();
//			window.setTitle("Testing");
//			window.addInputListener(new WindowInputListener() {
//				@Override
//				public void onKeyInput(@NotNull KeyCode code, int mods, @NotNull KeyAction action) {
//					if (code == KeyCode.ESCAPE)
//						renderSystem.runOnUIThread(RenderSystem::shutdown);
//				}
//			});
//			window.addWindowListener(new WindowListener() {
//				@Override
//				public void onClose() {
//					renderSystem.runOnUIThread(RenderSystem::shutdown);
//				}
//			});
//			window.setWindowSize(logicSize);
//
//			renderSystem.registerRenderer(new TestRenderer());
//
//			RenderSystem.loop();
		} finally {
			RenderSystem.shutdown();
		}
	}

	private static class TestRenderer implements Renderer {
		private static final Identifier TEST_SHADER = Identifier.withDefaultNamespace("test");

		private Mesh TEAPOT;

		@Override
		public void init(@NotNull RendererContext context) {
			Objects.requireNonNull(context, "context is null");

			TEAPOT = context
				.mesh()
				.build();

			context.preloadShader(TEST_SHADER);


		}

		@Override
		public void render(@NotNull RendererContext rendererContext, @NotNull RenderContext context) {
			context.push()
			       .shader(TEST_SHADER)
			       .mesh(TEAPOT)
			       .draw()
			       .pop();
		}
	}
}
