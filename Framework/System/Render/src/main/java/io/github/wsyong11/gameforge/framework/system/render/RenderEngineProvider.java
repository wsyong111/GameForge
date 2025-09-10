package io.github.wsyong11.gameforge.framework.system.render;

import io.github.wsyong11.gameforge.framework.system.render.engine.RenderEngine;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * 渲染引擎提供器
 */
public interface RenderEngineProvider {
	/**
	 * 获取渲染引擎的工厂函数
	 *
	 * @return 渲染引擎工厂
	 */
	@NotNull
	Function<RenderEngine.Lifecycle, RenderEngine> getFactory();
}
