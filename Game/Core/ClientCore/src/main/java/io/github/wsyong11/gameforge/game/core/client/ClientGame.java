package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.app.BootstrapContext;
import io.github.wsyong11.gameforge.framework.i18n.I18nManager;
import io.github.wsyong11.gameforge.framework.i18n.SimpleI18nManager;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.game.client.service.I18nService;
import io.github.wsyong11.gameforge.game.common.GameContext;
import io.github.wsyong11.gameforge.game.common.core.AbstractGame;
import io.github.wsyong11.gameforge.game.common.service.ServiceRegistry;
import io.github.wsyong11.gameforge.game.core.client.service.I18nServiceStub;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ClientGame extends AbstractGame {
	private static final Logger LOGGER = Log.getLogger();

	private static final Identifier ICON_PATH = Identifier.withDefaultNamespace("texture/icon.png");

	private final I18nManager i18nManager;

	private RenderThread renderThread;

	/**
	 * 实例化对象
	 *
	 * @param bootstrapContext 启动信息
	 */
	public ClientGame(@NotNull BootstrapContext bootstrapContext) {
		super(bootstrapContext, ResourcePath.of("assets"));

		this.i18nManager = new SimpleI18nManager(Locale.ENGLISH);

		this.renderThread = null;
	}

	@NotNull
	@Override
	protected GameContext createGameContext(@NotNull ServiceRegistry serviceRegistry) {
		return new ClientGameContext(this, serviceRegistry);
	}

	@Override
	protected void onStarting() throws Throwable {
		super.onStarting();

		ServiceRegistry serviceRegistry = this.getServiceRegistry();
		serviceRegistry.register(I18nService.class, new I18nServiceStub(this.i18nManager));

		this.renderThread = new RenderThread(this.getResourceManager(), this.isDebug());
		this.renderThread.setUncaughtExceptionHandler((ignored, e) -> {
			LOGGER.error("Fatal error! Rendering thread throw uncaught exception", e);
			this.requireStop();
		});
	}

	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();

		this.i18nManager.reload();
	}

	//
//	private static final Logger LOGGER = Log.getLogger();
//
//	private RenderThread renderThread;
//	private IInputManager inputManager;
//
//	public ClientGame(@NotNull StartupConfig config) {
//		super(config, ResourcePath.of("assets"));
//
//		this.renderThread = null;
//		this.inputManager = null;
//	}
//
//	// -------------------------------------------------------------------------------------------------------------- //
//
//	private void initRenderThread() throws RenderSystemInitiationException, InterruptedException {
//		IResourceManager resourceManager = this.getResourceManager();
//
//		// Init render thread
//		this.renderThread = new RenderThread(
//			resourceManager,
//			this.getConfig().isDebug()
//		);
//
//		this.renderThread.setUncaughtExceptionHandler((ignored, e) -> {
//			LOGGER.error("Fatal error! Rendering thread throw uncaught exception", e);
//			this.stop();
//		});
//
//		this.renderThread.init();
//
//		RenderSystem renderSystem = this.renderThread.getRenderSystem();
//
//		// Configuration window properties
//		Window window = this.renderThread.getWindow();
//
//		resourceManager.registerReloadListener(() -> {
//			Resource iconResource = resourceManager.getResource(ICON_PATH);
//			if (iconResource == null) {
//				LOGGER.debug("Cannot found window icon at location {}", ICON_PATH);
//				return;
//			}
//
//			Icon icon;
//			try (InputStream stream = iconResource.openStream()) {
//				icon = IconIO.read(stream);
//			} catch (IOException e) {
//				LOGGER.warn("Cannot load window icon from {}", ICON_PATH, e);
//				return;
//			}
//
//			renderSystem.runOnUIThread(() -> {
//				Icon oldIcon = window.getIcon();
//				if (oldIcon != null)
//					oldIcon.close();
//
//				window.setIcon(icon);
//			});
//		});
//
//		// Testing
//		renderSystem.registerRenderer(new Renderer() {
//			Identifier SHADER = Identifier.parse("game:test");
//
//			@Override
//			public void render(@NotNull RenderContext context) {
//				context.push() // 入栈
//				       .shader(SHADER) // 使用着色器
//				       .beginMesh() // 开始构建Mesh
//				       .vertex(-0.5F, -0.5F, 0.0F).color(1.0F, 0.0F, 0.0F).end() // 声明顶点
//				       .vertex(0.5F, -0.5F, 0.0F).color(0.0F, 1.0F, 0.0F).end() // 声明顶点
//				       .vertex(0.0F, 0.5F, 0.0F).color(0.0F, 0.0F, 1.0F).end() // 声明顶点
//				       .endMesh() // 结束构建顶点
//				       .draw() // 绘制上次的Mesh
//				       .pop(); // 出栈
//			}
//
//			@Override
//			public boolean shouldRender() {
//				return true;
//			}
//
//			@Override
//			public boolean isDirty() {
//				return true;
//			}
//		});
//	}
//
//	private void initInputManager() {
//		this.inputManager = new ();
//
//		Window window = this.renderThread.getWindow();
//		window.addWindowListener(new WindowListener() {
//			@Override
//			public void onClose() {
//				requireStop();
//			}
//		});
//		window.addInputListener(new InputManagerWindowListener(this.inputManager));
//	}
//
//	@Override
//	protected void onStarting() throws Throwable {
//		super.onStarting();
//
//		this.initRenderThread();
//		this.initInputManager();
//	}
//
//	// -------------------------------------------------------------------------------------------------------------- //
//
//	@Override
//	protected void tick(long currentTick) {
//		super.tick(currentTick);
//
//		// 检测渲染线程是否存活，否则进行退出
//		ILifecycle renderThreadLifecycle = this.renderThread.getLifecycle();
//		if (renderThreadLifecycle.getState() == LifecycleState.ERROR) {
//			LOGGER.error("Rendering thread state == ERROR, stopping main thread");
//			this.requireStop();
//		}
//	}
//
//	@Override
//	protected void onRunning() throws Throwable {
//		super.onRunning();
//
//		this.renderThread.runThread();
//		this.mainLoop();
//	}
//
//	// -------------------------------------------------------------------------------------------------------------- //
//
//	@Override
//	protected void onStopping() throws Throwable {
//		super.onStopping();
//
//		// Free window icon
//		Window window = this.renderThread.getWindow();
//		Icon windowIcon = window.getIcon();
//		if (windowIcon != null) {
//			window.setIcon(null);
//			windowIcon.close();
//		}
//
//		this.renderThread.close();
//		LOGGER.info("Waiting render thread exit");
//		this.renderThread.join();
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
}
