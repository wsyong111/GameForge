package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.input.DefaultInputManager;
import io.github.wsyong11.gameforge.framework.system.input.InputManager;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.render.RenderSystem;
import io.github.wsyong11.gameforge.framework.system.render.Renderer;
import io.github.wsyong11.gameforge.framework.system.render.context.RenderContext;
import io.github.wsyong11.gameforge.framework.system.render.ex.RenderSystemInitiationException;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.window.Window;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowListener;
import io.github.wsyong11.gameforge.game.common.core.AbstractGame;
import io.github.wsyong11.gameforge.game.common.core.StartupConfig;
import io.github.wsyong11.gameforge.game.common.tick.TickManager;
import org.jetbrains.annotations.NotNull;

public class ClientGame extends AbstractGame {
	private static final Logger LOGGER = Log.getLogger();

	private RenderThread renderThread;
	private InputManager inputManager;

	public ClientGame(@NotNull StartupConfig config) {
		super(config, ResourcePath.of("assets"));

		this.renderThread = null;
		this.inputManager = null;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void initRenderThread() throws RenderSystemInitiationException, InterruptedException {
		ResourceManager resourceManager = this.getResourceManager();

		this.renderThread = new RenderThread(
			resourceManager,
			this.getConfig().isDebug()
		);
		this.renderThread.init();

		RenderSystem renderSystem = this.renderThread.getRenderSystem();
		renderSystem.registerRenderer(new Renderer() {
			Identifier SHADER = Identifier.parse("game:test");

			@Override
			public void render(@NotNull RenderContext context) {
				context.push() // 入栈
				       .shader(SHADER) // 使用着色器
				       .beginMesh() // 开始构建Mesh
				       .vertex(-0.5F, -0.5F, 0.0F).color(1.0F, 0.0F, 0.0F).end() // 声明顶点
				       .vertex( 0.5F, -0.5F, 0.0F).color(0.0F, 1.0F, 0.0F).end() // 声明顶点
				       .vertex( 0.0F,  0.5F, 0.0F).color(0.0F, 0.0F, 1.0F).end() // 声明顶点
				       .endMesh() // 结束构建顶点
				       .draw() // 绘制上次的Mesh
				       .pop(); // 出栈
			}

			@Override
			public boolean shouldRender() {
				return true;
			}

			@Override
			public boolean isDirty() {
				return true;
			}
		});
	}

	private void initInputManager() {
		this.inputManager = new DefaultInputManager();

		Window window = this.renderThread.getWindow();
		window.addWindowListener(new WindowListener() {
			@Override
			public void onClose() {
				requireStop();
			}
		});
	}

	@Override
	protected void onStarting() throws Throwable {
		super.onStarting();

		this.initRenderThread();
		this.initInputManager();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();

		this.renderThread.runThread();
		this.mainLoop();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	protected void onStopping() throws Throwable {
		super.onStopping();

		this.renderThread.close();
		LOGGER.info("Waiting render thread exit");
		this.renderThread.join();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
