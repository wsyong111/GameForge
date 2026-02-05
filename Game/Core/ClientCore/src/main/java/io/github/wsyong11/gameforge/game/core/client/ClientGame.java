package io.github.wsyong11.gameforge.game.core.client;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.app.BootstrapContext;
import io.github.wsyong11.gameforge.framework.i18n.I18nManager;
import io.github.wsyong11.gameforge.framework.i18n.SimpleI18nManager;
import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.system.audio.AudioSystem;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.OpenALAudioEngine;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.render.RenderSystem;
import io.github.wsyong11.gameforge.framework.system.render.impl.opengl.OpenGL330RenderEngine;
import io.github.wsyong11.gameforge.framework.system.resource.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.window.Window;
import io.github.wsyong11.gameforge.framework.system.window.icon.IconIO;
import io.github.wsyong11.gameforge.framework.system.window.impl.glfw.GLFWWindowManager;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowInputListener;
import io.github.wsyong11.gameforge.framework.system.window.listener.WindowListener;
import io.github.wsyong11.gameforge.game.client.service.I18nService;
import io.github.wsyong11.gameforge.game.common.GameContext;
import io.github.wsyong11.gameforge.game.common.GameEnvConfig;
import io.github.wsyong11.gameforge.game.common.core.AbstractGame;
import io.github.wsyong11.gameforge.game.common.service.ServiceRegistry;
import io.github.wsyong11.gameforge.game.core.client.service.I18nServiceStub;
import io.github.wsyong11.gameforge.util.io.CallbackPrintStream;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.Library;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ClientGame extends AbstractGame {
	private static final Logger LOGGER = Log.getLogger();

	private static final Identifier ICON_PATH = Identifier.withDefaultNamespace("texture/icon.png");

	private static final Identifier TEST_BGM = Identifier.withDefaultNamespace("sound/test_bgm.ogg");

	private final I18nManager i18nManager;

//	private RenderThread renderThread;

	/**
	 * 实例化对象
	 *
	 * @param bootstrapContext 启动信息
	 */
	public ClientGame(@NotNull BootstrapContext bootstrapContext) {
		super(bootstrapContext, ResourcePath.of("assets"));

		this.i18nManager = new SimpleI18nManager(Locale.ENGLISH);

//		this.renderThread = null;
	}

	@NotNull
	@Override
	protected GameContext createGameContext(@NotNull ServiceRegistry serviceRegistry) {
		return new ClientGameContext(this, serviceRegistry);
	}

	@Override
	protected void onPreStarting() throws Throwable {
		super.onPreStarting();
		this.initLWJGL();
	}

	private void initLWJGL() {
		GameEnvConfig envConfig = this.getEnvConfig();

		LOGGER.debug("Configuration and initialize lwjgl");

		Logger lwjglLogger = Log.getLogger("LWJGL");

		Configuration.DEBUG.set(envConfig.isDebug());
		Configuration.DEBUG_STREAM.set(new CallbackPrintStream(message -> {
			String msg = message.trim();
			if (msg.isEmpty())
				return;

			if (msg.startsWith("[LWJGL]"))
				lwjglLogger.trace(msg.substring(7).trim());
			else
				lwjglLogger.trace(msg);
		}, StandardCharsets.UTF_8));

		Configuration.DISABLE_CHECKS.set(!envConfig.isDebug());

		Library.initialize();
	}

	@Override
	protected void onStarting() throws Throwable {
		super.onStarting();

		ResourceManager resourceManager = this.getResourceManager();

		ServiceRegistry serviceRegistry = this.getServiceRegistry();
		serviceRegistry.register(I18nService.class, new I18nServiceStub(this.i18nManager));

		AudioSystem audioSystem = AudioSystem.init(() -> OpenALAudioEngine::new, resourceManager);
		AudioManager audioManager = audioSystem.getAudioManager();
		audioManager.preload(TEST_BGM);
		audioManager.awaitPreload();

		// TODO: 2025/11/19 I18n keys load
//		resourceManager.registerReloadListener(this.i18nManager::reload);

//		this.renderThread = new RenderThread(resourceManager, new Vector2i(800, 600), this.isDebug());
//		this.renderThread.setUncaughtExceptionHandler((ignored, e) -> {
//			LOGGER.error("Fatal error! Rendering thread throw uncaught exception", e);
//			this.requireStop();
//		});
	}

	@Override
	protected void onPostStarting() throws Throwable {
		super.onPostStarting();
	}

	@Override
	protected void onRunning() throws Throwable {
		super.onRunning();

		ResourceManager resourceManager = this.getResourceManager();

		RenderSystem renderSystem = RenderSystem.init(
			resourceManager,
			new Vector2i(800, 600),
			true,
			() -> OpenGL330RenderEngine::new,
			() -> GLFWWindowManager::new
		);

		Window window = renderSystem.getWindow();
//		resourceManager.registerReloadListener(() -> {
		Resource iconResource = resourceManager.getResource(ICON_PATH);
		if (iconResource != null) {
			try (InputStream stream = iconResource.openStream()) {
				window.setIcon(IconIO.read(stream));
			} catch (IOException e) {
				LOGGER.warn("Failed to load window icon from location {}", ICON_PATH, e);
			}
		} else {
			LOGGER.warn("Failed to load window icon from location {}", ICON_PATH);
		}
//		});

		window.addInputListener(new WindowInputListener() {
			@Override
			public void onKeyInput(@NotNull KeyCode code, int mods, @NotNull KeyAction action) {
				if (code == KeyCode.E) {
					renderSystem.runOnUIThread(RenderSystem::shutdown);
				}
			}
		});
		window.addWindowListener(new WindowListener() {
			@Override
			public void onClose() {
				renderSystem.runOnUIThread(RenderSystem::shutdown);
			}
		});

//		renderSystem.registerRenderer(new TestRenderer());

		AudioSystem audioSystem = AudioSystem.getInstance();
		AudioManager audioManager = audioSystem.getAudioManager();
		Audio bgm = audioManager.getAudio(TEST_BGM);
		System.out.println(bgm);

		RenderSystem.loop();
	}

	@Override
	protected void onPreDestroyed() throws Throwable {
		super.onPreDestroyed();
		AudioSystem.shutdown();
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
